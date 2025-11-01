package com.microshop.elearningbackend.learning.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LockAccountRequestDto {
    private int studentId;
    private String reason;
}
