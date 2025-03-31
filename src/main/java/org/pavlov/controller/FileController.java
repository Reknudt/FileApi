package org.pavlov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.pavlov.dto.response.FileInfoDto;
import org.pavlov.dto.response.ResponseMessage;
import org.pavlov.model.File;
import org.pavlov.service.CountService;
import org.pavlov.service.FileService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/api/file")
//@SecurityRequirement(name = "Keycloak")
@Tag(name = "File API")
public class FileController {

    private final CountService countService;
    private final FileService fileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Count bytes and save file", description = "Provide file to save")
//    @ApiResponse
    public ResponseEntity<ResponseMessage> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            fileService.saveFile(file);
            Long totalSize = countService.countCharsInFileByDivide(file);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Uploaded the file successfully: " + file.getOriginalFilename() + ", file contains: " + totalSize + " bytes"));
        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Could not upload the file: " + file.getOriginalFilename() + "!"));
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get file by id", description = "Provide id to download file")
    public ResponseEntity<byte[]> getById(@PathVariable Long id) {
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

    @GetMapping("/{id}/about")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get info about file", description = "Provide file `id`")
    public FileInfoDto getFileInfoById(@PathVariable Long id) {
        return fileService.getFileInfo(id);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get file by userId", description = "Provide `id` and `userId` to download file")
    public List<FileInfoDto> getAllUserFilesInfo(@PathVariable Long userId) {
        return fileService.getFilesByUserId(userId);
    }

    @GetMapping
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all files", description = "Provide all the files (for admin only)")
    public List<File> getAllFilesInfo() {
        return fileService.getAllFiles();
    }

    @PutMapping("/{id}/assignUser")
    @Operation(summary = "Assign user to file", description = "Provide file `id` and `userId` to assign")
    @ApiResponse(responseCode = "200", description = "File access updated", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid form filling", content = @Content)
    public void assignUser(@PathVariable Long id, @RequestParam @Valid Long userId) {
        fileService.assignUser(id, userId);
    }

    @PutMapping("{id}/removeUser")
    @Operation(summary = "Remove user from file", description = "Provide file `id` and `userId` to remove")
    @ApiResponse(responseCode = "200", description = "File access updated", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid form filling", content = @Content)
    public void removeUser(@PathVariable Long id, @RequestParam @Valid Long userId) {
        fileService.removeUser(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete file", description = "Provide file id to delete file")
    @ApiResponse(responseCode = "204", description = "File deleted", content = @Content)
    public ResponseEntity<ResponseMessage> deleteById(@PathVariable Long id) {
        try {
            fileService.deleteFile(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("File with Id " + id + " successfully deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Could not delete the file with Id " + id + "!"));
        }
    }
}