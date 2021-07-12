package com.soap.rest.domain.service;

import com.soap.rest.BusinessTemplateApplication;
import com.soap.rest.domain.model.entity.FileEntity;
import com.soap.rest.domain.repository.FileRepository;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
        if (fileEntity.getType().equals("text/xml")) {
            InputStream targetStream = new ByteArrayInputStream(fileEntity.getData());
            Definitions defs = parser.parse(targetStream);
            for (PortType pt : defs.getPortTypes()) {
                for (Operation op : pt.getOperations()) {
                    list.add(op.getName());
                }
            }
        } else {
            try {
                String wsdlFileName = "";
                File destDir = new File(".");
                byte[] buffer = new byte[1024];
                InputStream targetStream = new ByteArrayInputStream(fileEntity.getData());
                ZipInputStream zis = new ZipInputStream(targetStream);
                ZipEntry zipEntry = zis.getNextEntry();
                while (zipEntry != null) {
                    File newFile = newFile(destDir, zipEntry);
                    if (zipEntry.isDirectory()) {
                        if (!newFile.isDirectory() && !newFile.mkdirs()) {
                            throw new IOException("Failed to create directory " + newFile);
                        }
                    } else {
                        // fix for Windows-created archives
                        File parent = newFile.getParentFile();
                        if (!parent.isDirectory() && !parent.mkdirs()) {
                            throw new IOException("Failed to create directory " + parent);
                        }
                        //
                        String ext = FilenameUtils.getExtension(newFile.getName());
                        if(ext.equals("xml")) {
                            wsdlFileName = zipEntry.getName();
                        }

                        // write file content
                        FileOutputStream fos = new FileOutputStream(newFile);
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                    }
                    zipEntry = zis.getNextEntry();
                }
                zis.closeEntry();
                zis.close();
                InputStream xmlStream = new FileInputStream(wsdlFileName);
                Definitions defs = parser.parse(xmlStream);
                for (PortType pt : defs.getPortTypes()) {
                    for (Operation op : pt.getOperations()) {
                        list.add(op.getName());
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        return list;
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

}
