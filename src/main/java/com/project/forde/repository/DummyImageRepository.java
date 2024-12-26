package com.project.forde.repository;

import com.project.forde.entity.DummyImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DummyImageRepository extends JpaRepository<DummyImage, Long> {
    List<DummyImage> findAllByImageIdIn(List<Long> imageIds);
}
