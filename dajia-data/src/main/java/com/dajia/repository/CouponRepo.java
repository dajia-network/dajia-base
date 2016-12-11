package com.dajia.repository;

import com.dajia.domain.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by huhaonan on 2016/10/20.
 */
public interface CouponRepo extends CrudRepository<Coupon, Long>  {

    /**
     * 根据类型、状态、过期时间 查找 按照过期时间倒序
     */
    Page<Coupon> findByTypeInAndStatusInAndGmtExpiredBeforeOrderByGmtExpiredDesc(List<Integer> typeList, List<Integer> statusList, Pageable pageable);

}
