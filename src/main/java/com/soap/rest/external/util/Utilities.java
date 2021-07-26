package com.soap.rest.external.util;

import com.soap.rest.domain.model.entity.FileEntity;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class Utilities {
    Logger logger = LoggerFactory.getLogger(Utilities.class);

    @Value("${destination.root-path}")
    private String destinationPath;

    public String unzip(FileEntity fileEntity) throws IOException {
        String wsdlFileName = "";
        File destDir = new File(destinationPath);
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
                String ext = FilenameUtils.getExtension(newFile.getName());
                if (ext.equals("xml")) {
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

        return wsdlFileName;
    }

    public String unGzip(FileEntity fileEntity) {
        String wsdlName = "";
        String tempTar = destinationPath + "\\temp.tar";

        try {
            File tarFile = new File(tempTar);
            // Calling method to decompress file
            tarFile = deCompressGZipFile(fileEntity, tarFile);
            File destFile = new File(destinationPath);
            if (!destFile.exists()) {
                destFile.mkdir();
            }
            // Calling method to untar file
            wsdlName = unTarFile(tarFile, destFile);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return wsdlName;

    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private String unTarFile(File tarFile, File destFile) throws IOException {
        String wsdlFileName = "";
        FileInputStream fis = new FileInputStream(tarFile);
        TarArchiveInputStream tis = new TarArchiveInputStream(fis);
        TarArchiveEntry tarEntry = null;

        while ((tarEntry = tis.getNextTarEntry()) != null) {
            File outputFile = new File(destFile + File.separator + tarEntry.getName());
            if (tarEntry.isDirectory()) {
                if (!outputFile.exists()) {
                    outputFile.mkdirs();
                }
            } else {
                outputFile.getParentFile().mkdirs();
                String ext = FilenameUtils.getExtension(outputFile.getName());
                if (ext.equals("xml")) {
                    wsdlFileName = outputFile.getName();
                }
                FileOutputStream fos = new FileOutputStream(outputFile);
                IOUtils.copy(tis, fos);
                fos.close();
            }
        }
        tis.close();
        return wsdlFileName;
    }


    private File deCompressGZipFile(FileEntity fileEntity, File tarFile) throws IOException {
        InputStream targetStream = new ByteArrayInputStream(fileEntity.getData());

        GZIPInputStream gZIPInputStream = new GZIPInputStream(targetStream);

        FileOutputStream fos = new FileOutputStream(tarFile);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = gZIPInputStream.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }
        fos.close();
        gZIPInputStream.close();
        return tarFile;
    }

    private static String getFileName(File inputFile, String outputFolder) {
        return outputFolder + File.separator +
                inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
    }
}
