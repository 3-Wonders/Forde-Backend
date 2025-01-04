package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Draft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DraftRepository extends JpaRepository<Draft, Long> {
    Long countByUploader(AppUser uploader);
}
