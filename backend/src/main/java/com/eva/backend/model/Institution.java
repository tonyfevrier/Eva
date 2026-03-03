package com.eva.backend.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "institutions")
@Entity
public class Institution {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToMany(mappedBy = "institutions")
    private List<User> users;

    @ToString.Exclude
    @OneToMany(mappedBy = "institution")
    private List<Experimentation> experimentations;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String town;

    @Email(message = "Email invalide.")
    @Column(nullable = false, unique = true)
    private String contactMail;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Integer studentsNumber;

    @Column(nullable = false)
    private String socialStatus;
    
    private String institutionSpecifities;    
    private String studentsSpecificities;
    private String teachersSpecificities;
}
