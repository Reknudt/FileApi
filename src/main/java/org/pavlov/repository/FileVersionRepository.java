package org.pavlov.repository;

import org.hibernate.annotations.processing.HQL;
import org.pavlov.model.FileVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface FileVersionRepository extends JpaRepository<FileVersion, Long> {

    Page<FileVersion> findAllByFileId(Pageable pageable, @Param("fileId") Long fileId);

    @HQL("DELETE FROM FileVersion fv WHERE fv.fileId = :fileId AND fv.version = :version")
//    @Modifying
//    @Query("delete from FileVersion fv where fv.fileId = :fileId AND fv.version = :version")
    void deleteByFileIdAndVersion(Long fileId, long version);

//    @HQL("DELETE FROM FileVersion fv WHERE dv.fileId = :fileId")
    void deleteByFileId(Long fileId);
}
