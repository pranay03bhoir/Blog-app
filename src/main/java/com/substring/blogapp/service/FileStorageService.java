package com.substring.blogapp.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeFile(MultipartFile file, String folder);

    Resource loadFileAsResource(String fileName);

    String getContentType(String fileName);
}
