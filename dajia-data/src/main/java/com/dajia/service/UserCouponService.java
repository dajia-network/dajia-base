package com.dajia.service;

import com.dajia.domain.Coupon;
import com.dajia.domain.CouponConstants;
import com.dajia.domain.UserCoupon;
import com.dajia.repository.CouponRepo;
import com.dajia.repository.UserCouponRepo;
import com.dajia.repository.UserRepo;
import com.dajia.util.DajiaResult;
import com.dajia.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.dajia.domain.CouponConstants.*;
import static com.dajia.util.ResultConstants.*;

/**
 * 用户优惠券领取、消费、作废服务
 *
 * Created by huhaonan on 2016/10/20.
 */

@Service
public class UserCouponService {

    final static Logger logger = LoggerFactory.getLogger(UserCouponService.class);

    final static List<Integer> STATUS_SHOULD_NOT_REQUEST_AGAIN = Arrays.asList(STATUS_ACTIVE, STATUS_EXPIRED, STATUS_USED, STATUS_GIVEUP);

    @Autowired
    public CouponRepo couponRepo;

    @Autowired
    public UserCouponRepo userCouponRepo;

    @Autowired
    public UserRepo userRepo;

    @Autowired
    public CouponService couponService;

    @Autowired
    public UserService userService;

    /**
     * 私有方法 不需要校验Coupon 但是需要校验User 外部调用只能通过 publishCoupons 来调用
     *
     * @param c
     * @param userId
     * @param createdBy
     * @return
     */
    private DajiaResult doPublishCoupon(Coupon c, Long userId, String createdBy) {

        DajiaResult canFindUser = userService.canFoundUser(userId);
        if(canFindUser.isNotSucceed()) {
            return canFindUser;
        }

        UserCoupon userCoupon = new UserCoupon(c, userId, null, createdBy);
        try {
            userCouponRepo.save(userCoupon);
            return DajiaResult.success();
        } catch (Exception ex) {
            logger.error("save user coupon failed, couponId={}, userId={}, msg={}", c.id, userId, ex);
            return DajiaResult.systemError("优惠券发放失败,系统异常", null, ex);
        }
    }

    /**
     * 给用户发放优惠券
     *
     * @param couponId
     * @param users
     */
    public DajiaResult publishCoupons (Long couponId, List<Long> users, String createdBy) {

        if (null == couponId || couponId <= 0) {
            return DajiaResult.inputError("优惠券发放失败,优惠券ID不存在", null);
        }

        if (null == users || users.size() <= 0) {
            return DajiaResult.inputError("优惠券发放不成功, 用户信息为空", null);
        }

        DajiaResult couponResult = couponService.findOne(couponId);
        if (couponResult.isNotSucceed()) {
            return couponResult;
        }

        Coupon c = (Coupon) couponResult.data;

        int totalSuccess = 0;
        DajiaResult succeed;
        for (Long userId : users) {
            succeed = doPublishCoupon(c, userId, createdBy);
            if (succeed.succeed) {
                totalSuccess++;
            }
        }

        if (totalSuccess == 0) {
           return DajiaResult.operationFail("优惠券批量发放全部失败", null);
        }

        int total = users.size();
        String userMsg = totalSuccess == total ? "优惠券批量发放全部成功" :
                                    String.format("优惠券批量发放部分成功, 预计发放%d, 成功%d, 失败%d", total, totalSuccess, total - totalSuccess);
        return DajiaResult.successReturn(userMsg, null, null);
    }

    /**
     * 用户是否可以领取某张优惠券
     *
     * 如果有状态为
     * 1. 已发放
     * 2. 已使用
     * 3. 已过期
     * 4. 自己主动放弃
     *
     * 则视为已经领取过优惠券
     */
    public DajiaResult canRequest (Long userId, Long couponId) {

        DajiaResult canFindUser = userService.canFoundUser(userId);
        if(canFindUser.isNotSucceed()) {
            return canFindUser;
        }

        DajiaResult couponExistsResult = couponService.findOne(couponId);
        if (couponExistsResult.isNotSucceed()) {
            return couponExistsResult;
        }

        DajiaResult result;
        try {
            int userAlreadyHave = userCouponRepo.countByUserIdAndCouponIdAndStatusIn(userId, couponId, STATUS_SHOULD_NOT_REQUEST_AGAIN);

            if (userAlreadyHave > 0) {
                result =  DajiaResult.successReturn("已经领过相同的优惠券", null, Boolean.FALSE);
            } else {
                result = DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, Boolean.TRUE);
            }
        } catch (Exception ex) {
            logger.error("canRequest-countByUserIdAndCouponId failed, user={}, coupon={}", userId, couponId, ex);
            result = DajiaResult.systemError(COMMON_MSG_QUERY_FAILED + ",系统异常", null, ex);
        }
        return result;
    }

    /**
     * 批量作废用户手上的优惠券
     *
     * @param userId
     * @param coupons
     * @return
     */
    public DajiaResult cancelUserCoupons (Long userId, List<Long> coupons) {
        return DajiaResult.fail().setMessages("暂不支持的操作", null, null);
    }

    /**
     * 用户消费优惠券
     *
     * 调用这个函数的函数必须包含在一个事务中
     *
     * @param userId
     * @param couponPkList
     * @return
     */
    @Transactional
    public DajiaResult consumeUserCoupons (Long userId, Long orderId, List<Long> couponPkList) {

        DajiaResult canFindUser = userService.canFoundUser(userId);
        if(canFindUser.isNotSucceed()) {
            return canFindUser;
        }

        /**  只能消费存在的并且状态是"未使用"状态的优惠券 **/
        try {
            int consumed = userCouponRepo.updateStatusByPK(couponPkList, userId, orderId, STATUS_USED, STATUS_ACTIVE);
            if (consumed != couponPkList.size()) {
                String msg = String.format("消费优惠券失败,优惠券信息不正确[%d/%d]", consumed, couponPkList.size());
                return DajiaResult.inputError(msg, null);
            }
            return DajiaResult.success();
        } catch (Exception ex) {
            return DajiaResult.systemError("消费优惠券失败,系统异常", null, ex);
        }
    }

    /**
     * 查找用户手上的优惠券
     *
     * @param userId
     * @return
     */
    public DajiaResult getUserCoupons(Long userId, Pageable pageable) {

        DajiaResult canFindUser = userService.canFoundUser(userId);
        if(canFindUser.isNotSucceed()) {
            return canFindUser;
        }

        try {
            Page<UserCoupon> coupons = userCouponRepo.findByUserIdOrderByStatusAscGmtExpiredDesc(userId, pageable);
            return DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, coupons);
        } catch (Exception ex) {
            return DajiaResult.systemError("获取优惠券列表失败,系统异常", null, ex);
        }
    }

    /**
     * 根据status列表来查找用户手上的优惠券
     *
     * @param userId
     * @return
     */
    public DajiaResult getUserCouponsByStatus(Long userId, List<Integer> statusList) {

        DajiaResult canFindUser = userService.canFoundUser(userId);
        if(canFindUser.isNotSucceed()) {
            return canFindUser;
        }

        try {
            return DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, userCouponRepo.findByUserIdAndStatusIn(userId, Arrays.asList(STATUS_ACTIVE)));
        } catch (Exception ex) {
            return DajiaResult.systemError("系统异常", null, ex);
        }
    }

    /**
     * TODO 优惠券规则引擎
     *
     * 对于某些商品的组合 查找当前用户可以使用的优惠券列表
     *
     * @param userId
     * @return
     */
    public DajiaResult getAvailableConponsWhenBuy(Long userId) {

        DajiaResult canFindUser = userService.canFoundUser(userId);
        if(canFindUser.isNotSucceed()) {
            return canFindUser;
        }

        Long now = System.currentTimeMillis();
        Long tomorrowMorning = DateUtil.tomorrowMorning();

        /**
         * 规则:
         * 1. UserId匹配
         * 2. 订单ID为空
         * 3. status为可使用
         * 4. 限制使用开始时间 <= now
         * 5. 限制使用过期时间 >= tomorrow morning
         */
        try {
            List<UserCoupon> availableCoupons =  userCouponRepo.findByUserIdAndStatusInAndOrderIdIsNullAndGmtExpiredGreaterThanEqualAndGmtStartLessThanEqual(userId, Arrays.asList(STATUS_ACTIVE), tomorrowMorning, now);
            return DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, availableCoupons);
        } catch (Exception ex) {
            return DajiaResult.systemError("系统异常", null, ex);
        }
    }

    /**
     * 计算用这些优惠券一共能减去多少钱
     *
     * TODO 复杂规则
     *
     * @param couponPkList
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public DajiaResult getTotalCutOffWithCoupons(List<Long> couponPkList, Long userId) {

        DajiaResult canFindUser = userService.canFoundUser(userId);
        if(canFindUser.isNotSucceed()) {
            return canFindUser;
        }

        if(null == couponPkList || couponPkList.isEmpty()) {
            return DajiaResult.successReturn("未选择优惠券,没有优惠", null, BigDecimal.ZERO);
        }

        /**
         * 三种异常
         *
         * 1. 伪造优惠券ID
         * 2. 使用别人的优惠券ID
         * 3. 优惠券状态不正常
         */
        List<UserCoupon> userCoupons;
        try {
            userCoupons = userCouponRepo.findByUserIdAndIdInAndStatus(userId, couponPkList, CouponConstants.STATUS_ACTIVE);
        } catch (Exception ex) {
            return DajiaResult.systemError("使用优惠券失败,系统异常", null, ex);
        }

        if (null == userCoupons || userCoupons.size() < couponPkList.size()) {
            return DajiaResult.inputError("存在不能使用的优惠券", null);
        }

        int total = 0;
        for(UserCoupon userCoupon : userCoupons) {
            if (null == userCoupon.value || userCoupon.value <= 0) {
                return DajiaResult.inputError("存在金额错误的优惠券", null);
            }
            total += userCoupon.value;
        }

        return DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, new BigDecimal(total));
    }


    public DajiaResult findCouponsByOrderId(Long orderId, Long userId) {
        DajiaResult canFindUser = userService.canFoundUser(userId);
        if(canFindUser.isNotSucceed()) {
            return canFindUser;
        }
        try {
            List<UserCoupon> userCoupons = userCouponRepo.findByOrderIdAndUserId(orderId, userId);
            return DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, userCoupons);
        } catch (Exception ex) {
            return DajiaResult.systemError(COMMON_MSG_QUERY_FAILED, null, ex);
        }
    }

}
