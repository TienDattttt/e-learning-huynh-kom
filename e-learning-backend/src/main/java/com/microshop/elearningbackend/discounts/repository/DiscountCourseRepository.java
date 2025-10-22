package com.microshop.elearningbackend.discounts.repository;

import com.microshop.elearningbackend.entity.DiscountCourse;
import com.microshop.elearningbackend.entity.DiscountCourseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiscountCourseRepository extends JpaRepository<DiscountCourse, DiscountCourseId> {
    @Query("""
           select case when count(dc)>0 then true else false end
           from DiscountCourse dc
           where dc.id.discountId = :discountId and dc.id.courseId = :courseId
           """)
    boolean existsLink(Integer discountId, Integer courseId);

    @Modifying
    @Query("""
           delete from DiscountCourse dc
           where dc.id.discountId = :discountId and dc.id.courseId = :courseId
           """)
    void deleteLink(Integer discountId, Integer courseId);

    @Query("""
           select case when count(dc)>0 then true else false end
           from DiscountCourse dc
           where dc.id.discountId = :discountId
           """)
    boolean existsAnyCourseLinked(Integer discountId);

    @Query("select dc from DiscountCourse dc where dc.id.discountId = :discountId")
    List<DiscountCourse> findAllByDiscountId(Integer discountId);
}

