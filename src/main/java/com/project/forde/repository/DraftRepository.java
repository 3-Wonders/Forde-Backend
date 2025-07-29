package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Draft;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DraftRepository extends JpaRepository<Draft, Long> {
    List<Draft> findTop10ByUploaderOrderByDraftIdDesc(
            @Param("uploader") AppUser uploader
    );
    Long countByUploader(AppUser uploader);
}
