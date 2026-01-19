package com.eva.backend.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "institutions")
@Entity
public class Institution {
    @Id
    private Long id;

    @ManyToMany(mappedBy = "institutions")
    private List<User> users;

    private String town;

    @Email(message = "Email invalide.")
    private String contactMail;

    private String category;
    private Number studentsNumber;
    private String socialStatus;
    private String institutionSpecifities;    
    private String studentsSpecificities;
    private String teachersSpecificities;



}
