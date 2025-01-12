package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.InterestTag;
import com.project.forde.entity.composite.InterestTagPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InterestTagRepository extends JpaRepository<InterestTag, InterestTagPK> {
    List<InterestTag> findAllById_AppUser(AppUser appUser);
}
