package org.pavlov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/count")
@SecurityRequirement(name = "Keycloak")
@Tag(
        name = "Подсчет байтов",
        description = "Взаимодействие для подсчета количества байтов в файле")
public class FileController {

    private final CountService countService;
    private final FileService fileService;

    @PostMapping
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Подсчет байтов и сохранения файла",
            description = "Для подсчета добавьте файл")
    public ResponseEntity<ResponseMessage> handleFileUpload(@RequestParam("file") MultipartFile file) {

        String message = "";
        try {
            fileService.saveFile(file);
            Long totalSize = countService.countCharsInFileByDivide(file);

            message = "Uploaded the file successfully: " + file.getOriginalFilename() + ", file contains: " + totalSize + " bytes";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Получение файла по ID",
            description = "Для получения отправьте ID")
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

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление файла по ID",
            description = "Для удаления отправьте ID файла")
    public ResponseEntity<ResponseMessage> deleteByID(@PathVariable Long id) {

        String message = "";
        try {
            fileService.deleteFile(id);

            message = "File with ID " + id + " successfully deleted";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not delete the file with ID " + id + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }
}