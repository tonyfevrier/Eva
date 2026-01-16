package com.eva.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "contexts")
@Entity
public class PedagogicalContext {

    @Id
    private Long id;

    @OneToOne
    @MapsId 
    @JoinColumn(name = "expe_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore 
    private Experimentation experimentation;

    @Column(nullable = false)
    private String problem;

    @Column(nullable = false)
    private String affiliation;

    @Column(nullable = false)
    private String classroom;

    @Column(nullable = false)
    private String oldPedagogy;

    @Column(nullable = false)
    private String newPedagogy;

    @Column(nullable = false)
    private String groupsDescription;
}
