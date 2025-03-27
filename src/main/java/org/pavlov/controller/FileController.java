package org.pavlov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.pavlov.dto.response.ResponseMessage;
import org.pavlov.model.File;
import org.pavlov.service.CountService;
import org.pavlov.service.FileService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/file")
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Взаимодействия с файлом")
public class FileController {

    private final CountService countService;
    private final FileService fileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Подсчет байтов и сохранения файла", description = "Для подсчета добавьте файл")
//    @ApiResponse
    public ResponseEntity<ResponseMessage> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            fileService.saveFile(file);
            Long totalSize = countService.countCharsInFileByDivide(file);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Uploaded the file successfully: " + file.getOriginalFilename() + ", file contains: " + totalSize + " bytes"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Could not upload the file: " + file.getOriginalFilename() + "!"));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Получение файла по ID", description = "Для получения отправьте ID")
    public ResponseEntity<byte[]> getByID(@PathVariable Long id) {
        File file = fileService.getFile(id);
        HttpHeaders headers = new HttpHeaders();
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(file.getName())
                        .build().toString())
                .headers(headers)
                .body(file.getData());
    }


    @PutMapping("assignUser/{id}")
    @Operation(summary = "Assign user to file", description = "Provide user id as path variable and file's id " +
            "as a parameter to assign new user to task")
    @ApiResponse(responseCode = "200", description = "File access updated", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid form filling", content = @Content)
    public void assignUser(@PathVariable Long id, @RequestParam @Valid Long userId) {
        fileService.assignUser(id, taskId);
    }

    @PutMapping("removeTask/{id}")
    @Operation(summary = "Remove user from file", description = "Provide user id and task id to remove user from file")
    @ApiResponse(responseCode = "200", description = "Employee updated", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid form filling", content = @Content)
    public void removeUser(@PathVariable Long id, @RequestParam @Valid Long userId) {
        fileService.removeUser(id, taskId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete file", description = "Provide file id to delete file")
    @ApiResponse(responseCode = "204", description = "File deleted", content = @Content)
    public ResponseEntity<ResponseMessage> deleteByID(@PathVariable Long id) {
        try {
            fileService.deleteFile(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("File with ID " + id + " successfully deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Could not delete the file with ID " + id + "!"));
        }
    }
}