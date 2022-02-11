package com.mrn.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
/*
* Class that saves image to your specified location.
* If there is no upload directory then will be created one.
* */
public class FileUploadUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {

        // get the path
        Path uploadPath = Paths.get(uploadDir);

        // create directory into the path if !exists
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(fileInputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IOException("Could not save file: " + fileName, ex);
        }
    }

    public static void cleanDir(String dir) {

        Path dirPath = Paths.get(dir);

        try {
            Files.list(dirPath).forEach(file -> {
                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    } catch (IOException ex) {
                        LOGGER.error("Could not delete file: " + file);
//                      System.out.println("Could not delete file: " + file);
                    }
                }
            });
        } catch (IOException ex) {
            LOGGER.info("Could not list directory: " + dirPath);
//            System.out.println("Could not list directory: " + dirPath);
        }
    }
}
