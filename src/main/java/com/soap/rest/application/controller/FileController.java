package com.soap.rest.application.controller;

import com.soap.rest.domain.model.dto.northbound.response.ResponseMessage;
import com.soap.rest.domain.model.entity.FileEntity;
import com.soap.rest.domain.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<FileEntity> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            FileEntity fileEntity = fileStorageService.store(file);
            message = "Uploaded the file successfully ";
            responseMessage.setMessage(message);
            responseMessage.setId(fileEntity.getId());
            return ResponseEntity.status(HttpStatus.OK).body(fileEntity);
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            responseMessage.setMessage(message);
            responseMessage.setId("");
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        FileEntity fileDB = fileStorageService.getFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(fileDB.getData());
    }

    @GetMapping("/parse/{id}")
    public ResponseEntity<List> parseWSDL(@PathVariable("id") String id) {
        List<String> list = fileStorageService.parse(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
