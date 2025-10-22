package com.microshop.elearningbackend.courses.repository;

import com.microshop.elearningbackend.entity.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Cours, Integer>, JpaSpecificationExecutor<Cours> {
}
