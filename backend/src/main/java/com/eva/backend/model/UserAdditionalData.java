package com.eva.backend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usersAdditional")
@Entity
public class UserAdditionalData {
    
    // id qui sera celui du user associé grâce à MapsId
    @Id
    private Long id;

    @OneToOne
    @MapsId // colonne qui contiendra l'id du user (qui est l'id de l'objet aussi)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Size(min = 1)
    private String affiliation;

    private boolean acceptMap;

    private boolean acceptContact;

    private String street;

    private String postcode;

    private String town;

    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$", 
         message = "Numéro de téléphone invalide")
    private String phone;
}
