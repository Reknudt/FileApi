package org.pavlov.repository;

import org.pavlov.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

}