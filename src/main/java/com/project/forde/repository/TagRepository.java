package com.project.forde.repository;

import com.project.forde.entity.Tag;
import com.project.forde.entity.composite.BoardTagPK;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, BoardTagPK> {
    List<Tag> findAllByTagIdIn(List<Long> tagIds);
    @Query("SELECT t FROM Tag t WHERE t.tagName LIKE CONCAT(:keyword, '%') ORDER BY LENGTH(t.tagName), t.tagCount DESC LIMIT 10")
    List<Tag> findTop10ByTagNameStartingWith(String keyword);

    List<Tag> findAllByOrderByTagCountDesc(Pageable pageable);
}
