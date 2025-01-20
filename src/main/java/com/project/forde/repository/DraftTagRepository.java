package com.project.forde.repository;

import com.project.forde.entity.Draft;
import com.project.forde.entity.DraftTag;
import com.project.forde.entity.composite.DraftTagPK;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DraftTagRepository extends JpaRepository<DraftTag, DraftTagPK> {
    @Query(
            "SELECT dt FROM DraftTag dt " +
                    "JOIN FETCH dt.draftTagPK.tag t " +
                    "WHERE dt.draftTagPK.draft = :draft"
    )
    List<DraftTag> findAllByDraftTagPK_Draft(Draft draft);

    @Query(
            "SELECT dt FROM DraftTag dt " +
                    "JOIN FETCH dt.draftTagPK.tag t " +
                    "WHERE dt.draftTagPK.draft IN :drafts"
    )
    List<DraftTag> findAllByDraftTagPK_DraftIn(List<Draft> drafts);
}
