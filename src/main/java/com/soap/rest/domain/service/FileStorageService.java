package com.soap.rest.domain.service;

import com.soap.rest.domain.model.entity.FileEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageService {

    FileEntity store(MultipartFile file) throws IOException;

    FileEntity getFile(String id);

    ResponseEntity<List<String>> parse(String id);
}
