package org.pavlov.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
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
    private byte[] data;

    @Schema(description = "File name")
    @NotBlank(message = "File name must not be empty.")
    private String name;

    @Schema(description = "File type")
    @NotBlank(message = "File type must not be empty.")
    private String type;

    @Schema(description = "File's creation date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateOfCreation = LocalDateTime.now();

    @Schema(description = "File version")
    @Positive
    private long version = 1;

    @Schema(description = "File status")
    @Enumerated(EnumType.STRING)
    private FileStatus status = FileStatus.OK;

    @Schema(description = "Info about changes")
    @Size(max = 255)
    private String note = "Uploaded";

    @ManyToMany
    @JoinTable(
            name = "file_user",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;
}