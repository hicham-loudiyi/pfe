package com.example.myapp.service;

import com.example.myapp.config.ConfigurationPropertyValue;
import com.example.myapp.exceptions.FileSizeException;
import com.example.myapp.exceptions.InvalidFileTypeException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class FileValidator {

    private final ConfigurationPropertyValue config;

    public void validateImageFile(MultipartFile file) throws FileSizeException, InvalidFileTypeException {
        if (file.getSize() > config.getMaxImageSize()) {
            throw new FileSizeException("La taille du fichier dépasse 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileTypeException("Seules les images sont acceptées");
        }
    }
}
