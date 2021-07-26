package com.soap.rest.domain.service;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;
import com.soap.rest.domain.model.entity.FileEntity;
import com.soap.rest.domain.repository.FileRepository;
import com.soap.rest.external.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileStorageServiceImpl <T> implements FileStorageService {

    Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private Utilities utilities;

    @Value("${destination.root-path}")
    private String destinationPath;

    @Override
    public FileEntity store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        FileEntity FileDB = new FileEntity(fileName, file.getContentType(), file.getBytes());

        return fileRepository.save(FileDB);
    }

    @Override
    public FileEntity getFile(String id) {
        return fileRepository.findById(id).get();
    }

    @Override
    public ResponseEntity<List<String>> parse(String id) {
        logger.info("[NBREQ] - id of file to parse: {}", id);
        FileEntity fileEntity = getFile(id);
        List<String> list = new ArrayList<>();
        if (fileEntity.getType().equals("text/xml")) {
            InputStream targetStream = new ByteArrayInputStream(fileEntity.getData());
            list = parseWsdl((T) targetStream);
        } else if (fileEntity.getType().equals("application/x-zip-compressed")) {
            try {
                String wsdlFileName = utilities.unzip(fileEntity);
                String filePath = destinationPath + "\\" + wsdlFileName;
                logger.info("File path of wsdl {}", filePath);
                list = parseWsdl((T) filePath);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } else if (fileEntity.getType().equals("application/x-gzip")) {
            try {
                String wsdlFileName = utilities.unGzip(fileEntity);
                String filePath = destinationPath  + "\\" + wsdlFileName;
                logger.info("File path of wsdl {}", filePath);
                list = parseWsdl((T) filePath);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        logger.info("[NBRES] - List of operations {}", list);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    private List<String> parseWsdl(T t){
        List<String> list = new ArrayList<>();
        WSDLParser parser = new WSDLParser();
        Definitions defs;
        if (t instanceof String) {
            defs = parser.parse((String) t);
        } else {
            defs = parser.parse((InputStream) t);
        }
        for (PortType pt : defs.getPortTypes()) {
            for (Operation op : pt.getOperations()) {
                list.add(op.getName());
            }
        }
        return list;
    }

}
