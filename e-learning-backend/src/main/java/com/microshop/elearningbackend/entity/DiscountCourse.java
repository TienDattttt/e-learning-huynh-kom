package com.microshop.elearningbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "DiscountCourse", schema = "dbo", indexes = {
        @Index(name = "IX_DiscountCourse_DiscountId", columnList = "DiscountId"),
        @Index(name = "IX_DiscountCourse_CourseId", columnList = "CourseId")
})
public class DiscountCourse {
    @EmbeddedId
    private DiscountCourseId id;

    @MapsId("discountId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "DiscountId", nullable = false)
    private Discount discount;

    @MapsId("courseId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CourseId", nullable = false)
    private Cours course;

}