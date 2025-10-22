package com.microshop.elearningbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class DiscountCourseId implements Serializable {
    private static final long serialVersionUID = 1850635321532218486L;
    @Column(name = "DiscountId", nullable = false)
    private Integer discountId;

    @Column(name = "CourseId", nullable = false)
    private Integer courseId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DiscountCourseId entity = (DiscountCourseId) o;
        return Objects.equals(this.discountId, entity.discountId) &&
                Objects.equals(this.courseId, entity.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discountId, courseId);
    }

}