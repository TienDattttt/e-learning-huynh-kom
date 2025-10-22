package com.microshop.elearningbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "LearningProgress", schema = "dbo", indexes = {
        @Index(name = "IX_LP_User_Course", columnList = "UserId, CourseId"),
        @Index(name = "IX_LP_Lesson", columnList = "LessonId")
}, uniqueConstraints = {
        @UniqueConstraint(name = "UQ_LearningProgress_User_Lesson", columnNames = {"UserId", "LessonId"})
})
public class LearningProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProgressId", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CourseId", nullable = false)
    private Cours course;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "LessonId", nullable = false)
    private CourseLesson lesson;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ProgressPercent", nullable = false)
    private Integer progressPercent;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "IsCompleted", nullable = false)
    private Boolean isCompleted = false;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;

}