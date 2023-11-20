package com.klass.server.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;


@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;
    @NotNull(message = "Role cannot be null")
    @Pattern(regexp = "admin|student|instructor", message = "Role must be admin, student or instructor")
    private String role;
    @NotNull(message = "Name cannot be null")
    private String name;
    @NotNull(message = "Last name cannot be null")
    private String lastName;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "Password cannot be null")
    @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
    private String password;
    private String picture;
    private List<String> courses; // Referencias a cursos
    private String passwordResetCode;
    private Date createdAt;
    private Date updatedAt;

}
