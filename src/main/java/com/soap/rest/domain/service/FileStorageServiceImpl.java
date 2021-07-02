package com.soap.rest.domain.service;

import com.soap.rest.domain.model.entity.FileEntity;
import com.soap.rest.domain.repository.FileRepository;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private FileRepository fileRepository;


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
    public List<String> parse(String id) {
        FileEntity fileEntity = getFile(id);
        List<String> list = new ArrayList<>();
        WSDLParser parser = new WSDLParser();
        InputStream targetStream = new ByteArrayInputStream(fileEntity.getData());
        Definitions defs = parser.parse(targetStream);
        for (PortType pt : defs.getPortTypes()) {
            for (Operation op : pt.getOperations()) {
                list.add(op.getName().toLowerCase());
            }
        }
        return list;
    }

}
