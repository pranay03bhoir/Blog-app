package com.substring.blogapp.service.serviceImpl;

import com.substring.blogapp.exceptions.ResourceNotFoundException;
import com.substring.blogapp.service.FileStorageService;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Autowired(required = false)
    private MinioClient minioClient;

    @Value("${spring.minio.bucket-name:blog-app}")
    private String bucketName;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private boolean useMinio = false;
    private Path localUploadPath;

    @PostConstruct
    public void init() {
        try {
            localUploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(localUploadPath);

            if (minioClient != null) {
                boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
                if (!bucketExists) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                }
                useMinio = true;
                log.info("Successfully connected to MinIO bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.warn("MinIO is not reachable, using local storage fallback at {}: {}", localUploadPath, e.getMessage());
            useMinio = false;
        }
    }

    @Override
    public String storeFile(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file.");
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg");
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        } else {
            extension = ".jpg";
        }

        String uniqueFileName = (folder != null && !folder.isBlank() ? folder + "_" : "")
                + UUID.randomUUID() + extension;

        if (useMinio && minioClient != null) {
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(uniqueFileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
                return uniqueFileName;
            } catch (Exception e) {
                log.warn("Failed to upload to MinIO, falling back to local disk: {}", e.getMessage());
            }
        }

        // Local disk storage
        try {
            Path targetLocation = localUploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFileName;
        } catch (Exception e) {
            throw new RuntimeException("Could not store file " + uniqueFileName + ". Please try again!", e);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        if (useMinio && minioClient != null) {
            try {
                InputStream stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .build()
                );
                byte[] bytes = stream.readAllBytes();
                return new ByteArrayResource(bytes);
            } catch (Exception e) {
                log.warn("Could not retrieve file {} from MinIO: {}", fileName, e.getMessage());
            }
        }

        // Local storage fallback
        try {
            Path filePath = localUploadPath.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("File not found: " + fileName);
        }
    }

    @Override
    public String getContentType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }
}
