package com.dajia.service;

import com.dajia.domain.Coupon;
import com.dajia.domain.UserCoupon;
import com.dajia.repository.CouponRepo;
import com.dajia.repository.UserCouponRepo;
import com.dajia.repository.UserRepo;
import com.dajia.util.DajiaResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
            return DajiaResult.inputError("优惠券ID不存在", null);
        }

        Coupon c;
        try {
            c = couponRepo.findOne(couponId);
            if (null == c) {
                return DajiaResult.notFound("优惠券ID不存在", null);
            }
        } catch (Exception ex) {
            return DajiaResult.systemError("系统异常", null, ex);
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
            int consumed = userCouponRepo.batchUpdateStatusAndOrderId(userId, orderId, couponIds, UserCoupon.STATUS_USED, UserCoupon.STATUS_NOT_USED);
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
            Page<UserCoupon> coupons = userCouponRepo.findByUserId(userId, pageable);
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

}
