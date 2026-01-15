
package com.eva.backend.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "users")
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    @Email(message = "Email invalide.")
    @Column(unique = true, nullable = false)
    private String mail;   
    
    @Column(nullable = false)
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
    private String password;

    @Builder.Default
    private String role = "USER";

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAdditionalData additionalData;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experimentation> experimentations;

    @Override
    public String getUsername(){
        return mail;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities(){
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
