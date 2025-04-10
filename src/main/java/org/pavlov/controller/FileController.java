package org.pavlov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.pavlov.dto.response.FileInfoDto;
import org.pavlov.dto.response.FileVersionInfoDto;
import org.pavlov.dto.response.PageFileResponse;
import org.pavlov.dto.response.ResponseMessage;
import org.pavlov.model.File;
import org.pavlov.service.FileService;
import org.pavlov.service.FileVersionService;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@RestController
@RequestMapping("/api/file")
//@SecurityRequirement(name = "Keycloak")
@Tag(name = "File API")
public class FileController {

    private final FileVersionService fileVersionService;
    private final FileService fileService;

    @GetMapping("/{id}/content")
    @Operation(summary = "Get file content by id", description = "Provide `id` and page parameters to access file content")
    public ResponseEntity<PageFileResponse> getFileContentByPage(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1024") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber) {
        try {
            PageFileResponse response = fileService.getFileContentByPage(id, pageSize, pageNumber);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get file download by id", description = "Provide id to download file")
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

    @GetMapping("/user/{userId}")               //???????
    @Operation(summary = "Get files info by userId", description = "Provide `id` and `userId` to get info about user's files")
    public List<FileInfoDto> getAllUserFilesInfo(@PathVariable Long userId) {
        return fileService.getFilesByUserId(userId);
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    @Operation(summary = "Get all files", description = "Provide all the files (for admin only)")
    public Page<FileInfoDto> getAllFilesInfo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fileService.getAllFiles(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Count bytes and save file", description = "Provide file to save")
//    @ApiResponse
    public ResponseEntity<ResponseMessage> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("dateOfCreation") Optional<LocalDateTime> dateTime) {
        try {
            fileService.saveFile(file, dateTime);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Uploaded file successfully: " + file.getOriginalFilename()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Could not upload the file: " + file.getOriginalFilename() + "!"));
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(e.getMessage()));
        }
    }

    //-- USERS

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

    //---- VERSIONS

    @GetMapping("/{id}/versions")
    @Operation(summary = "Get all file versions", description = "Get all versions of a file")
    public Page<FileVersionInfoDto> getAllFileVersions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam @Valid Long fileId) {
        Pageable pageable = PageRequest.of(page, size);
        return fileVersionService.getAllFileVersionsByFileId(pageable, fileId);
    }

    @GetMapping("/{id}/versions/{versionId}")
    @Operation(summary = "Get specific file version", description = "Get a specific version of a file")
    public FileVersionInfoDto getFileVersion(@PathVariable Long id, @PathVariable Long versionId) {
        return fileVersionService.getFileVersionInfo(id, versionId);
    }

    @PatchMapping("/{id}/versions/{versionId}/restore")
    @Operation(summary = "Restore file version", description = "Provide fileId and versionId to restore a specific version of a file")
    public ResponseEntity<ResponseMessage> restoreFileVersion(@PathVariable Long id, @PathVariable Long versionId) {
        try {
            fileService.restoreFileVersion(id, versionId);
            return ResponseEntity.ok(new ResponseMessage("File version restored successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    //--

    @PatchMapping("/{id}/content")
    @Operation(summary = "Update file content on a specific page",
            description = "Provide file id, content and page parameters to update the content of a file on a specific page")
    public ResponseEntity<ResponseMessage> updateFileContentOnPage(
            @PathVariable Long id,
            @RequestParam int pageNumber,
            @RequestParam(defaultValue = "1024") int pageSize,
            @RequestParam("note") Optional<String> note,
            @RequestBody String newContent) {
        try {
            fileService.updateFileContentOnPage(id, pageNumber, pageSize, note, newContent);
            return ResponseEntity.ok(new ResponseMessage("File content updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/name")
    @Operation(summary = "Update file name", description = "Update the name of a file")
    public ResponseEntity<ResponseMessage> updateFileName(
            @PathVariable Long id,
            @RequestParam("note") Optional<String> note,
            @RequestBody String newFileName) {
        try {
            fileService.updateFileName(id, note, newFileName);
            return ResponseEntity.ok(new ResponseMessage("File name updated to " + newFileName + " successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
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

    @DeleteMapping("/{id}/versions/{version}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete file version", description = "Provide file `id` and `version` to delete FileVersion")
    @ApiResponse(responseCode = "204", description = "File deleted", content = @Content)
    public ResponseEntity<ResponseMessage> deleteFileVersionById(@PathVariable("id") Long id, @PathVariable("version") long version) {
        try {
            fileService.deleteFileVersion(id, version);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("FileVersion with version " + version + " of file " + id + " successfully deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(e.getMessage()));
//                    new ResponseMessage("Could not delete fileVersion with Id " + version + "!"));
        }
    }

    @DeleteMapping("/{id}/versions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete all file versions", description = "Provide file `id` and `version` to delete FileVersion")
    @ApiResponse(responseCode = "204", description = "File deleted", content = @Content)
    public ResponseEntity<ResponseMessage> deleteFileVersion(@PathVariable Long id) {
        try {
            fileService.deleteFileVersions(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("FileVersions of file " + id + " successfully deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                    new ResponseMessage("Could not delete fileVersions!"));
        }
    }

    @DeleteMapping("/{id}/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete file and its versions")
    @ApiResponse(responseCode = "204", description = "File deleted", content = @Content)
    public ResponseEntity<ResponseMessage> deleteFileAndVersions(@PathVariable Long id) {
        try {
            fileService.deleteAll(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("File " + id + " and its versions successfully deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                    new ResponseMessage("Could not delete file with fileVersions!"));
        }
    }
}