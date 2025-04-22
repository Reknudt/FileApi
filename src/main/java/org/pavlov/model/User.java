package org.pavlov.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "`user`")
public class User implements Serializable {

    @Schema(description = "User id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Keycloak ID (generating automatically)")
    private String keycloakId;

    @Schema(description = "User name")
//    @NotNull(message = "Name must be not null.")
    @Size(max = 255, message = "Name length must be smaller than 255 symbols.")
    private String username;

    @Schema(description = "First name")
    @NotNull(message = "Name must be not null.")
    @Size(max = 255, message = "Name length must be smaller than 255 symbols.")
    private String firstName;

    @Schema(description = "Second name")
    @NotNull(message = "Name must be not null.")
    @Size(max = 255, message = "Name length must be smaller than 255 symbols.")
    private String lastName;

    @Schema(description = "User's password")
    @NotNull(message = "Password must be not null.")
    private String password;

    @Schema(description = "User's email")
    @NotNull(message = "Email must not be null.")
    private String email;

    @Schema(description = "User's phone number")
    @NotNull(message = "Phone must not be null.")
    private String phone;

    @Schema(description = "User's date of birth")
    @Temporal(TemporalType.DATE)
    private LocalDate dateOfBirth;

    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private List<File> files;
}
