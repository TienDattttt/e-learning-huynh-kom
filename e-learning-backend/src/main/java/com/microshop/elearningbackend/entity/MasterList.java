package com.microshop.elearningbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "MasterList", schema = "dbo", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_MasterList_Code_Group", columnNames = {"MasterListCode", "MasterListGroupCde"})
})
public class MasterList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MasterListId", nullable = false)
    private Integer id;

    @Column(name = "MasterListCode", nullable = false, length = 50)
    private String masterListCode;

    @Column(name = "MasterListGroupCde", nullable = false, length = 50)
    private String masterListGroupCde;

    @Nationalized
    @Column(name = "MasterListDefaultValue", length = 2000)
    private String masterListDefaultValue;

    @Nationalized
    @Column(name = "Description", length = 2000)
    private String description;

}