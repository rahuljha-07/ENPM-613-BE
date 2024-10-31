package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.CoursePurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;


public interface CoursePurchaseRepo extends JpaRepository<CoursePurchase, UUID> {

}
