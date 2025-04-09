package org.pavlov.service;

import lombok.RequiredArgsConstructor;
import org.pavlov.dto.response.FileInfoDto;
import org.pavlov.dto.response.FileVersionInfoDto;
import org.pavlov.mapper.FileVersionMapper;
import org.pavlov.model.FileVersion;
import org.pavlov.repository.FileVersionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FileVersionService {

    private final FileVersionRepository fileVersionRepository;
    private final FileVersionMapper fileVersionMapper;

//    public void createNewVersion(Long fileId, byte[] newContent) {
//        File fileEntity = fileRepository.findById(fileId)
//                .orElseThrow(() -> new RuntimeException("File not found"));
//
//        FileVersion fileVersion = fileVersionMapper.fileToFileVersion(fileEntity);
//
//        fileVersionRepository.save(fileVersion);
//
//        // Обновление текущего файла
//        fileEntity.setData(newContent);
//        fileRepository.save(fileEntity);
//    }

//    public FileReadDto getVersionContent(Long fileId, int version) {
//        return fileVersionRepository.findByFileIdAndVersion(fileId, version)
//                .orElseThrow(() -> new RuntimeException("Version not found")).getContent();
//    }

    public Page<FileVersionInfoDto> getAllFileVersionsByFileId(Pageable pageable, Long fileId) {
        Page<FileVersion> fileVersions = fileVersionRepository.findAllByFileId(pageable, fileId);
        return fileVersions.map(fileVersionMapper::entityToFileVersionInfoDto);
    }

    public FileVersionInfoDto getFileVersionInfo(Long fileId, long version) {
        FileVersion fileVersion = fileVersionRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("FileVersion not found"));
        return fileVersionMapper.entityToFileVersionInfoDto(fileVersion);
    }
}
