package com.project.forde.repository;

import com.project.forde.entity.Draft;
import com.project.forde.entity.DraftTag;
import com.project.forde.entity.composite.DraftTagPK;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DraftTagRepository extends JpaRepository<DraftTag, DraftTagPK> {
    @EntityGraph(attributePaths = {"draftTagPK.tag"})
    List<DraftTag> findAllByDraftTagPK_Draft(Draft board);
}
