package com.dajia.service;

import com.dajia.domain.Coupon;
import com.dajia.domain.UserCoupon;
import com.dajia.repository.CouponRepo;
import com.dajia.repository.UserCouponRepo;
import com.dajia.util.DajiaResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.dajia.util.ResultConstants.*;


/**
 * 优惠券Service
 *
 * Created by huhaonan on 2016/10/20.
 */
@Service
public class CouponService {

    final static Logger logger = LoggerFactory.getLogger(CouponService.class);

    @Autowired
    public CouponRepo couponRepo;

    @Autowired
    public UserCouponRepo userCouponRepo;

    /**
     * 新增优惠券
     *
     * @param example
     * @return
     */
    public DajiaResult addCoupon (Coupon example) {

        Coupon coupon = new Coupon();

        DajiaResult result = checkBeforeAdd(example);

        if (!result.isSucceed()) {
            return result;
        }

        try {
            coupon = couponRepo.save(coupon);
        } catch (Exception ex) {
            return DajiaResult.systemError(COMMON_MSG_SAVE_FAILED, null, ex);
        }

        return DajiaResult.successReturn(COMMON_MSG_SAVE_OK, null, coupon);
    }



    /**
     * 修改优惠券
     *
     * @param example
     * @return
     */
    public DajiaResult updateCoupon (Coupon example) {

        DajiaResult checkResult = checkBeforeUpdate(example);

        if (!checkResult.succeed) {
            return checkResult;
        }

        DajiaResult findResult = findOne(example.id);

        if (!findResult.succeed) {
            return findResult;
        }

        Coupon c = (Coupon) findResult.data;

        DajiaResult copyResult = copy(c, example);

        if(!copyResult.succeed) {
            return copyResult;
        }

        try {
            couponRepo.save(c);
        } catch (Exception ex) {
            return DajiaResult.systemError(COMMON_MSG_SAVE_FAILED, null, ex);
        }

        return DajiaResult.successReturn(COMMON_MSG_UPDATE_OK, null, c);
    }

    /**
     * 取消优惠券
     *
     * 此时需要需要还未使用的UserCoupon
     *
     * @param couponId
     * @return
     */
    @Transactional
    public DajiaResult cancelCoupon (long couponId) {

        Coupon example = new Coupon();
        example.id = couponId;
        example.status = Coupon.STATUS_CANCELED;

        DajiaResult result = updateCoupon(example);

        if(!result.succeed) {
            return result;
        }

        // 批量作废 UserCoupon
        try {
            int num = userCouponRepo.batchUpdateStatusByCouponId(couponId, UserCoupon.STATUS_CANCELED);
            return DajiaResult.success().setMessages(String.format("%s, 共%d用户优惠券被取消", COMMON_MSG_UPDATE_OK, num), null, null);

        } catch (Exception ex) {
            return DajiaResult.systemError("批量取消优惠券失败,系统异常", null, ex);
        }
    }


    /**
     * 合并
     * @param c
     * @param example
     */
    private DajiaResult copy(Coupon c, Coupon example) {

        if (null == example) {
            return DajiaResult.inputError("example is null", null);
        }

        if (StringUtils.isNotEmpty(example.name)) {
            c.name = example.name;
        }

        if (StringUtils.isNotEmpty(example.desc)) {
            c.desc = example.desc;
        }

        if (StringUtils.isNotEmpty(example.createdBy)) {
            c.createdBy = example.createdBy;
        }

        if (StringUtils.isNotEmpty(example.modifiedBy)) {
            c.modifiedBy = example.modifiedBy;
        }

        if (null != example.createdDate) {
            c.createdDate = example.createdDate;
        }

        if (null != example.modifiedDate) {
            c.modifiedDate = example.modifiedDate;
        }

        if (example.amount >= 0) {
            c.amount = example.amount;
        }

        if (example.status > 0) {
            c.status = example.status;
        }

        if (example.remain >= 0) {
            c.remain = example.remain;
        }

        return DajiaResult.successReturn(null, null, c);
    }


    /**
     * 新建优惠券的校验
     *
     * @param example
     * @return
     */
    private DajiaResult checkBeforeAdd(Coupon example) {
        DajiaResult result = checkCoupon(example);
        if (result.succeed) {
            if (null == example.remain || example.remain < example.amount) {
                example.remain = example.amount;
            }
        }
        return result;
    }

    /**
     * 修改优惠券的校验
     *
     * @param example
     * @return
     */
    public DajiaResult checkBeforeUpdate(Coupon example) {
        return checkCoupon(example);
    }

    /**
     * 优惠券的基础校验
     *
     * @param example
     * @return
     */
    private DajiaResult checkCoupon(Coupon example) {
        if (null == example) {
            return DajiaResult.inputError("优惠券信息为空", null);
        }

        if (null == example.amount || example.amount <= 0) {
            return DajiaResult.inputError("优惠券数量必须大于0", null);
        }

        if (!example.amount.equals(example.remain)) {
            example.remain = example.amount;
        }

        if (example.gmtExpired == null) {
            return DajiaResult.inputError("优惠券必须设置过期时间", null);
        }

        // TODO 过期时间应该是按天计算
        if (example.gmtExpired.before(new Date())) {
            return DajiaResult.inputError("优惠券过期时间不能早于当前时间", null);
        }

        if (example.value <= 0) {
            return DajiaResult.inputError("优惠券金额必须大于0", null);
        }

        if (example.type <= 0) {
            return DajiaResult.inputError("非法的优惠券类型", null);
        }

        if (!example.isActive()) {
            return DajiaResult.inputError("新建优惠券的状态必须是可用", null);
        }

        return DajiaResult.success();
    }

    /**
     * 按照ID查找
     *
     * @param couponId
     * @return
     */
    public DajiaResult findOne(Long couponId) {
        Coupon c;
        try {
            c = couponRepo.findOne(couponId);
            if (null == c) {
                return DajiaResult.notFound("该优惠券ID不存在", null);
            }
        } catch (Exception ex) {
            return DajiaResult.systemError(COMMON_MSG_QUERY_FAILED, null, ex);
        }
        return DajiaResult.successReturn(null, null, c);
    }


}
