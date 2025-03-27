package org.pavlov.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User implements Serializable {

    @Schema(description = "User id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "User name")
    @NotNull(message = "Name must be not null.")
    @Size(max = 255, message = "Name length must be smaller than 255 symbols.")
    private String name;

    @Schema(description = "User's encrypted password")
    @NotNull(message = "Password must be not null.")
    private String password;

    @Schema(description = "User's age'")
    @Min(16)
    private Integer age;

//    @ManyToMany
//    @JoinTable(
//            name = "user_files",
//            joinColumns = @JoinColumn(name = "employee_id"),
//            inverseJoinColumns = @JoinColumn(name = "task_id"))
//    private List<File> files;
}
