package org.pavlov.service;

import lombok.RequiredArgsConstructor;
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
