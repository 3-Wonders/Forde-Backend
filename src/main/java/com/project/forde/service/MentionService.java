package com.project.forde.service;

import com.project.forde.entity.Comment;
import com.project.forde.entity.Mention;
import com.project.forde.repository.MentionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentionService {
    private final MentionRepository mentionRepository;

    public List<Mention> getMentionIn(final List<Comment> comments) {
        return mentionRepository.findAllByMentionPK_CommentIn(comments);
    }
}
