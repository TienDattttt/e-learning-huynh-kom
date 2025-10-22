package com.microshop.elearningbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Courses", schema = "dbo", indexes = {
        @Index(name = "IX_Courses_Category", columnList = "CategoriesId"),
        @Index(name = "IX_Courses_UsersId", columnList = "UsersId")
})
public class Cours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CourseId", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "Name", nullable = false, length = 50)
    private String name;

    @Nationalized
    @Column(name = "Description", length = 250)
    private String description;

    @Nationalized
    @Column(name = "Image", length = 250)
    private String image;

    @Nationalized
    @Lob
    @Column(name = "Content")
    private String content;

    @Column(name = "Price")
    private Long price;

    @Column(name = "PromotionPrice")
    private Long promotionPrice;

    @ColumnDefault("getdate()")
    @Column(name = "DateCreated", nullable = false)
    private LocalDateTime dateCreated;

    @ColumnDefault("0")
    @Column(name = "Status", nullable = false)
    private Boolean status = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoriesId")
    private CourseCategory categories;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UsersId", nullable = false)
    private User users;

}