package com.project.forde.repository;

import com.project.forde.entity.Comment;
import com.project.forde.entity.Mention;
import com.project.forde.entity.composite.MentionPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentionRepository extends JpaRepository<Mention, MentionPK> {
    List<Mention> findAllByMentionPK_Comment(Comment comment);
}
