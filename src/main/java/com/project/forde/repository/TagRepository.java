package com.project.forde.repository;

import com.project.forde.entity.Tag;
import com.project.forde.entity.composite.BoardTagPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, BoardTagPK> {
    List<Tag> findAllByTagIdIn(List<Long> tagIds);
}
