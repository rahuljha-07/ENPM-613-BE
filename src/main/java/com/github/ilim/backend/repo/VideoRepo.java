package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoRepo extends JpaRepository<Video, UUID> {

}
