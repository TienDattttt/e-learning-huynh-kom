package com.microshop.elearningbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "CourseLessons", schema = "dbo", indexes = {
        @Index(name = "IX_Lessons_ChapterId", columnList = "ChapterId")
})
public class CourseLesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CourseLessonId", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "Name", nullable = false, length = 250)
    private String name;

    @Nationalized
    @Column(name = "VideoPath", length = 250)
    private String videoPath;

    @Nationalized
    @Column(name = "SlidePath", length = 250)
    private String slidePath;

    @Column(name = "TypeDocument", length = 50)
    private String typeDocument;

    @Column(name = "SortOrder")
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ChapterId", nullable = false)
    private Chapter chapter;

}