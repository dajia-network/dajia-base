package com.dajia.repository;

import com.dajia.domain.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 注意消耗优惠券应该是一个事务 (订单\商品\优惠券)
 *
 * 放到service去做
 *
 * Created by huhaonan on 2016/10/20.
 */
public interface UserCouponRepo extends CrudRepository<UserCoupon, Long> {

    /**
     * 查找某个用户手上的优惠券 可选参数包括优惠券类型、状态和过期时间
     * 类型主要用于判断这个券是否可以用作某个订单
     * @return
     */
    Page<UserCoupon> findByUserIdAndCouponIdInAndStatusInAndGmtExpiredBefore(
            Long userId,
            List<Long> couponIdList,
            List<Integer> statusList,
            Long gmtExpired,
            Pageable pageable
    );

    /**
     * 查找某种类型的优惠券一共发放了哪些
     */
    Page<UserCoupon> findByCouponIdInAndStatusIn(
            List<Long> couponIdList,
            List<Integer> statusList,
            Pageable pageable
    );

    /**
     * 查找用户手上所有的优惠券 显示在用户的优惠券列表中
     *
     * @param userId
     * @param pageable
     * @return
     */
    Page<UserCoupon> findByUserId(Long userId, Pageable pageable);


    @Modifying
    @Query("update UserCoupon uc set uc.status = :targetStatus where uc.couponId = :couponId")
    int batchUpdateStatusByCouponId(Long couponId, int targetStatus);

}
