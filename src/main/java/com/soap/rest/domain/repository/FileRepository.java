package com.soap.rest.domain.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.soap.rest.domain.model.entity.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, String>  {
}
