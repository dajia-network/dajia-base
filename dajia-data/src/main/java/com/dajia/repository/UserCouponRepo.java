package com.dajia.repository;

import com.dajia.domain.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

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
    Page<UserCoupon> findByUserIdOrderByStatusAscGmtExpiredDesc(Long userId, Pageable pageable);

    /**
     * TODO 是否需要指定status列表
     *
     * 查找用户手上是否已经有某张优惠券
     *
     * @param userId
     * @param couponId
     * @return
     */
    UserCoupon findByUserIdAndCouponId(Long userId, Long couponId);

    /**
     *
     * @param userId
     * @param idList
     * @return
     */
    List<UserCoupon> findByUserIdAndIdInAndStatus(Long userId, List<Long> idList, int status);


    /**
     * 查找用户手上可用的优惠券
     */
    List<UserCoupon> findByUserIdAndStatusInAndOrderIdIsNullAndGmtExpiredAfterAndGmtStartBefore(Long userId, List<Integer> statusList, Long expiredAfter, Long startBefore);


    List<UserCoupon> findByUserIdAndStatusIn(Long userId, List<Integer> statusList);


    @Modifying
    @Query("update UserCoupon uc set uc.status = :targetStatus where uc.couponId = :couponId and uc.status = :originStatus")
    int batchUpdateStatusByCouponId(@Param("couponId") Long couponId, @Param("targetStatus") int targetStatus, @Param("originStatus") int originStatus);

    @Modifying
    @Query("update UserCoupon uc set uc.status = :targetStatus, uc.orderId = :orderId where uc.id in :pkList and uc.userId = :userId and uc.status = :lastStatus")
    int updateStatusByPK(@Param("pkList") List<Long> pkList, @Param("userId") Long userId, @Param("orderId") Long orderId, @Param("targetStatus") int targetStatus, @Param("lastStatus") int lastStatus);


    @Query("select count(1) from UserCoupon where userId = :userId and couponId = :couponId and status in :statusList")
    Integer countByUserIdAndCouponIdAndStatusIn(@Param("userId") Long userId, @Param("couponId") Long couponId, @Param("statusList") List<Integer> statusList);

}
