package org.pavlov.repository;

import org.hibernate.annotations.processing.Find;
import org.hibernate.annotations.processing.HQL;
import org.pavlov.model.FileVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FileVersionRepository extends JpaRepository<FileVersion, Long> {

    Page<FileVersion> findAllByFileId(Pageable pageable, @Param("fileId") Long fileId);

    @Find
    Optional<FileVersion> findByIdAndVersion(long fileId, long version);

    @HQL("DELETE FROM FileVersion fv WHERE fv.fileId = :fileId AND fv.version = :version")
    void deleteByFileIdAndVersion(Long fileId, long version);

    @HQL("DELETE FROM FileVersion fv WHERE fv.fileId = :fileId")
    void deleteByFileId(Long fileId);
}
