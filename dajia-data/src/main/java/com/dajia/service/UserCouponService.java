package com.dajia.service;

import com.dajia.domain.Coupon;
import com.dajia.domain.CouponConstants;
import com.dajia.domain.UserCoupon;
import com.dajia.repository.CouponRepo;
import com.dajia.repository.UserCouponRepo;
import com.dajia.repository.UserRepo;
import com.dajia.util.DajiaResult;
import com.dajia.vo.OrderVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.dajia.util.ResultConstants.*;

/**
 * 用户优惠券领取、消费、作废服务
 *
 * Created by huhaonan on 2016/10/20.
 */

@Service
public class UserCouponService {

    final static Logger logger = LoggerFactory.getLogger(UserCouponService.class);

    @Autowired
    public CouponRepo couponRepo;

    @Autowired
    public UserCouponRepo userCouponRepo;

    @Autowired
    public UserRepo userRepo;

    /**
     * 给用户发放优惠券
     *
     * TODO 批量插入
     *
     * @param couponId
     * @param users
     */
    public DajiaResult publishCoupons (Long couponId, List<Long> users, String createdBy) {
        if (null == couponId || couponId <= 0) {
            return DajiaResult.inputError("优惠券发放失败,优惠券ID不存在", null);
        }

        Coupon c;
        try {
            c = couponRepo.findOne(couponId);
        } catch (Exception ex) {
            return DajiaResult.systemError("优惠券发放失败,系统异常", null, ex);
        }

        if (null == c) {
            return DajiaResult.notFound("优惠券发放失败,优惠券ID不存在", null);
        }

        int succeed = 0;
        for (Long userId : users) {
            UserCoupon userCoupon = new UserCoupon(c, userId, null, createdBy);
            try {
                userCouponRepo.save(userCoupon);
                succeed++;
            } catch (Exception ex) {
                logger.error("save user coupon failed, couponId={}, userId={}, msg={}", couponId, userId, ex.getMessage());
            }
        }

        int total = users.size();
        if (succeed == total) {
            return DajiaResult.success().setMessages("优惠券发放成功", null, null);
        } else {
            return DajiaResult.success().setMessages(String.format("预计发放%d, 成功%d, 失败%d", total, succeed, total - succeed), null, null);
        }
    }

    /**
     * 批量作废用户手上的优惠券
     *
     * @param userId
     * @param coupons
     * @return
     */
    public DajiaResult cancelUserCoupons (Long userId, List<Long> coupons) {
        return DajiaResult.fail();
    }

    /**
     * 用户消费优惠券
     *
     * 调用这个函数的函数必须包含在一个事务中
     *
     * @param userId
     * @param couponIds
     * @return
     */
    public DajiaResult consumeUserCoupons (Long userId, Long orderId, List<Long> couponIds) {

        if (!userRepo.exists(userId)) {
            return DajiaResult.inputError("消费优惠券失败,用户不存在", null);
        }

        // TODO 是否需要校验couponId是否存在
        // TODO 如果传入了不存在的couponId 将导致coupon更新数量不正确
        // TODO 可能导致优惠券使用失败

        // 只能消费"未使用"状态的优惠券
        try {
            int consumed = userCouponRepo.updateStatusByPK(couponIds, userId, orderId, CouponConstants.STATUS_USED);
            if (consumed != couponIds.size()) {
                return DajiaResult.systemError("未能成功使用全部选中的优惠券", null, null);
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

        if(!userRepo.exists(userId)) {
            return DajiaResult.notFound("获取优惠券列表失败,用户不存在", null);
        }

        try {
            Page<UserCoupon> coupons = userCouponRepo.findByUserIdOrderByStatusAscGmtExpiredDesc(userId, pageable);
            return DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, coupons);
        } catch (Exception ex) {
            return DajiaResult.systemError("获取优惠券列表失败,系统异常", null, ex);
        }
    }

    /**
     * TODO 优惠券规则引擎
     *
     * 对于某些商品的组合 查找当前用户可以使用的优惠券列表
     *
     * @param userId
     * @param productItems
     * @return
     */
    public DajiaResult getAvailableConponsWhenBuy(Long userId, List<Long> productItems) {
        if(!userRepo.exists(userId)) {
            return DajiaResult.notFound("获取优惠券列表失败,用户不存在", null);
        }
        return DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, null);
    }

    /**
     * 计算用这些优惠券一共能减去多少钱
     * TODO 复杂规则
     *
     * @param couponPkList
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public DajiaResult getTotalCutOffWithCoupons(List<Long> couponPkList, Long userId) {

        List<UserCoupon> userCoupons;

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
        try {
            userCoupons = userCouponRepo.findByUserIdAndIdInAndStatus(userId, couponPkList, CouponConstants.STATUS_ACTIVE);
        } catch (Exception ex) {
            return DajiaResult.systemError("使用优惠券失败,系统异常", null, ex);
        }

        if (null == userCoupons || userCoupons.size() < couponPkList.size()) {
            return DajiaResult.inputError("存在不能使用的优惠券", null);
        }

        BigDecimal totalCutOff = BigDecimal.ZERO;

        for(UserCoupon userCoupon : userCoupons) {
           if (null == userCoupon.value || userCoupon.value <= 0) {
                return DajiaResult.inputError("存在金额错误的优惠券", null);
           }
           totalCutOff = totalCutOff.add(new BigDecimal(userCoupon.value));
        }

        return DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, totalCutOff);
    }

}
