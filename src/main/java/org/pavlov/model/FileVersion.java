package org.pavlov.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "file_version")
public class FileVersion implements Serializable {

    @Schema(description = "File version id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "File id")
    private Long fileId;

    @Schema(description = "File data")
    private byte[] data;

    @Schema(description = "File name")
    private String name;

    @Schema(description = "File type")
    private String type;

    @Schema(description = "File's creation date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateOfCreation;

    @Schema(description = "File version")
    @Positive
    private long version;
}