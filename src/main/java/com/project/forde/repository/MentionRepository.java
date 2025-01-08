package com.project.forde.repository;

import com.project.forde.entity.Comment;
import com.project.forde.entity.Mention;
import com.project.forde.entity.composite.MentionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface MentionRepository extends JpaRepository<Mention, MentionPK> {
    List<Mention> findAllByMentionPK_Comment(Comment comment);
    @Query(
            "SELECT m FROM Mention m " +
                    "JOIN FETCH m.mentionPK.user mu " +
                    "WHERE m.mentionPK.comment IN :comments"
    )
    List<Mention> findAllByMentionPK_CommentIn(List<Comment> comments);
}
