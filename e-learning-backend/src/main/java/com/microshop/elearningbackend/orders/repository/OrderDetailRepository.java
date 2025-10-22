package com.microshop.elearningbackend.orders.repository;

import com.microshop.elearningbackend.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("""
           select case when count(od)>0 then true else false end
           from OrderDetail od
           where od.order.users.id = :userId
             and od.course.id = :courseId
             and od.order.status = 'SUCCESS'
           """)
    boolean existsPurchasedCourse(Integer userId, Integer courseId);

    @Query("""
           select od
           from OrderDetail od
           join fetch od.order o
           join fetch od.course c
           where o.users.id = :userId
             and o.status = 'SUCCESS'
           order by o.orderDate desc
           """)
    List<OrderDetail> findPurchasedDetailsByUser(Integer userId);
}
