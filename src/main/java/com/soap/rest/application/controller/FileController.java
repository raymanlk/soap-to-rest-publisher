package com.soap.rest.application.controller;

import com.soap.rest.domain.model.entity.FileEntity;
import com.soap.rest.domain.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/files")
public class FileController {

    Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<FileEntity> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileEntity fileEntity = fileStorageService.store(file);
            logger.info("Uploaded the file successfully");
            return ResponseEntity.status(HttpStatus.OK).body(fileEntity);
        } catch (Exception e) {
            logger.error("Could not upload the file: {}", file.getOriginalFilename());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
        }
    }

    @GetMapping("/parse/{id}")
    public ResponseEntity<List<String>> parseWSDL(@PathVariable("id") String id) {
        return fileStorageService.parse(id);
    }
}
