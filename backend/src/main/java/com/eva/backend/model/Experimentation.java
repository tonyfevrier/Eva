package com.eva.backend.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "experimentations")
@Entity
public class Experimentation {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private List<String> keywords;

    private String personalKeywords;

    @Column(nullable = false)
    private String protocol;

    //@Column(nullable = false) OneToMany (une innstitution peut faire plusieurs expés mais pas l'inverse)
    //private Institution institution;

    @Embedded
    private PedagogicalContext pedagogicalContext;

    @Column(nullable = false)
    private Boolean isSharingData;

    private String dataPath;
}
