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
@Table(name = "Chapter", schema = "dbo", indexes = {
        @Index(name = "IX_Chapter_CourseId", columnList = "CourseId")
})
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ChapterId", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "NameChapter", nullable = false, length = 200)
    private String nameChapter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CourseId", nullable = false)
    private Cours course;

    @Column(name = "OrderChapter")
    private Integer orderChapter;

}