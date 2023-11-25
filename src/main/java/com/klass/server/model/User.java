package com.klass.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private String id;
    @NotBlank(message = "Role cannot be blank or null")
    @Pattern(regexp = "admin|student|instructor", message = "Role must be admin, student or instructor")
    private String role;
    @NotBlank(message = "Name cannot be blank or null")
    private String name;
    @NotBlank(message = "Last name cannot be blank or null")
    private String lastName;
    // Unique field
    @Indexed(unique = true)
    @NotBlank(message = "Email cannot be blank or null")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Password cannot be blank or null")
    @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
    private String password;
    private String picture;
    private String passwordResetCode;


    // UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
