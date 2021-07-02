package com.soap.rest.domain.repository;

import com.soap.rest.domain.model.entity.ControllerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControllerRepository extends JpaRepository<ControllerEntity, Long> {
}
