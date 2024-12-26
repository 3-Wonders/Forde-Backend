package com.project.forde.repository;

import com.project.forde.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByTagIdIn(List<Long> tagIds);
}
