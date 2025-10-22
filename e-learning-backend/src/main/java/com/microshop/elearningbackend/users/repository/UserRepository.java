package com.microshop.elearningbackend.users.repository;

import com.microshop.elearningbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
