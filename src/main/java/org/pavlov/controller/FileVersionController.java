package org.pavlov.controller;

//import io.swagger.v3.oas.annotations.Operation;
//import lombok.AllArgsConstructor;
//import org.pavlov.dto.response.FileInfoDto;
//import org.pavlov.model.FileVersion;
//import org.pavlov.service.FileVersionService;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@AllArgsConstructor
//@RestController
//@RequestMapping("/api/fileVersions")
//public class FileVersionController {
//
//    private final FileVersionService fileVersionService;
//
//    @GetMapping
//    public List<FileVersion> getAllVersions(@PathVariable Long fileId) {
//        return fileVersionService.getAllVersions(fileId);
//    }
//
//    @GetMapping
//    @Operation(summary = "Get all files", description = "Provide all the files (for admin only)")
//    public Page<FileInfoDto> getAllFilesInfo(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return fileService.getAllFiles(pageable);
//    }
//
//    @GetMapping("/{version}")
//    public byte[] getVersionContent(@PathVariable Long fileId, @PathVariable int version) {
//        return fileVersionService.getVersionContent(fileId, version);
//    }
//
//    @PostMapping
//    public void createNewVersion(@PathVariable Long fileId, @RequestBody byte[] newContent) {
//        fileVersionService.createNewVersion(fileId, newContent);
//    }
//}
