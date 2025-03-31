package org.pavlov.repository;


import org.pavlov.dto.response.FileInfoDto;
import org.pavlov.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Query(value = """
            SELECT f.* FROM file f
            JOIN file_user fu on f.id = fu.file_id
            JOIN "user" u on fu.user_id = u.id
            WHERE u.id = :userId""",
            nativeQuery = true)
    List<File> findFilesByUserId(Long userId);

    @Query("SELECT new org.pavlov.dto.response.FileInfoDto(f.id, f.name, f.type, f.users) " +
            "FROM File f " +
            "WHERE f.id = :id")
    List<FileInfoDto> findFileInfo(@Param("id") Long id);


    //            SELECT f.id, f.name, f.type, JSON_AGG(ROW_TO_JSON(u2)) AS users
//            FROM file f
//            JOIN file_user fu on f.id = fu.file_id
//            JOIN "user" u2 on fu.user_id = u2.id
//            WHERE f.id = :id
//            GROUP BY f.id, f.name, f.type,
}