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

    public void validateMediaFile(MultipartFile file) throws FileSizeException, InvalidFileTypeException {
        // Vérification taille
        if (file.getSize() > config.getMaxMediaSize()) { // Ajoutez cette propriété dans ConfigurationPropertyValue
            throw new FileSizeException("La taille du fichier dépasse " + config.getMaxMediaSize()/1_000_000 + "MB");
        }

        // Vérification type
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
            throw new InvalidFileTypeException("Seules les images et vidéos sont acceptées");
        }
    }
}
