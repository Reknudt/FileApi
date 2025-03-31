package org.pavlov.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "file")
public class File implements Serializable {

    @Schema(description = "File id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "File data")
    @Column(name = "data", columnDefinition="bytea")
//    @NotEmpty(message = "File must not be empty.")
    private byte[] data;

    @Schema(description = "File name")
    @NotBlank(message = "File name must not be empty.")
    private String name;

    @Schema(description = "File type")
    @NotBlank(message = "File type must not be empty.")
    private String type;

    //date of upload

    @ManyToMany
    @JoinTable(
            name = "file_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id"))
    private List<User> users;
}