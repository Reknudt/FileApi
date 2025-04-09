package org.pavlov.repository;

import org.pavlov.model.FileVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileVersionRepository extends JpaRepository<FileVersion, Long> {

    Page<FileVersion> findAllByFileId(Pageable pageable, @Param("fileId") Long fileId);
}
