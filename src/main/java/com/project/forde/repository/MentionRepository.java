package com.project.forde.repository;

import com.project.forde.entity.Mention;
import com.project.forde.entity.composite.MentionPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentionRepository extends JpaRepository<Mention, MentionPK> {

}
