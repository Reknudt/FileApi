package org.pavlov.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "file")
public class File implements Serializable {

    @Schema(description = "File id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Schema(description = "File data")
    @Column(name = "data", columnDefinition="bytea")
//    @NotEmpty(message = "File must not be empty.")
    private byte[] data;

    @Schema(description = "File name")
    @NotNull(message = "File name must not be empty.")
    private String name;

    @Schema(description = "File type")
    @NotBlank(message = "File type must not be empty.")
    private String type;

    @Schema(description = "User id")
    @NotBlank
    private long userId;
}