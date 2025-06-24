package com.example.myapp.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class VideoProcessor {
    private static final int KEYFRAMES_COUNT = 5; // Nombre d'images clés à extraire
    private static final String IMAGE_FORMAT = "jpg";
    private static final String TEMP_DIR_PREFIX = "video_processing_";

    @Value("${ffmpeg.path:ffmpeg}")
    private String ffmpegPath;

    public String extractKeyFrames(MultipartFile videoFile) throws IOException {
        Path tempDir = null;
        try {
            // 1. Préparation de l'environnement temporaire
            tempDir = Files.createTempDirectory(TEMP_DIR_PREFIX);
            Path inputVideo = tempDir.resolve("input_" + System.currentTimeMillis() + "_" + videoFile.getOriginalFilename());

            // 2. Sauvegarde du fichier vidéo temporaire
            videoFile.transferTo(inputVideo);

            // 3. Extraction de la durée de la vidéo
            double duration = getVideoDuration(inputVideo);

            // 4. Extraction des images clés
            List<String> keyFrames = new ArrayList<>();
            for (int i = 0; i < KEYFRAMES_COUNT; i++) {
                double timestamp = duration * (i + 1) / (KEYFRAMES_COUNT + 1);
                Path outputImage = tempDir.resolve(String.format("frame_%02d.%s", i, IMAGE_FORMAT));

                extractFrameAtTime(inputVideo, outputImage, timestamp);
                keyFrames.add(encodeImageToBase64(outputImage));
            }

            // 5. Formatage du résultat (JSON avec les images et leurs timestamps)
            return formatKeyFramesResult(keyFrames, duration);
        } finally {
            // Nettoyage des fichiers temporaires
            if (tempDir != null) {
                cleanTempDirectory(tempDir);
            }
        }
    }

    private double getVideoDuration(Path videoFile) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
                "-i", videoFile.toString(),
                "-f", "null", "-"
        );

        try {
            Process process = pb.start();
            String errorOutput = new String(process.getErrorStream().readAllBytes());
            process.waitFor();

            // Extraction de la durée depuis la sortie d'erreur de FFmpeg
            Pattern pattern = Pattern.compile("Duration: (\\d+):(\\d+):(\\d+)\\.\\d+");
            Matcher matcher = pattern.matcher(errorOutput);

            if (matcher.find()) {
                int hours = Integer.parseInt(matcher.group(1));
                int minutes = Integer.parseInt(matcher.group(2));
                int seconds = Integer.parseInt(matcher.group(3));
                return hours * 3600 + minutes * 60 + seconds;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interruption pendant l'extraction de la durée", e);
        }

        throw new IOException("Impossible de déterminer la durée de la vidéo");
    }

    private void extractFrameAtTime(Path inputVideo, Path outputImage, double timestamp) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
                "-ss", String.valueOf(timestamp),
                "-i", inputVideo.toString(),
                "-vframes", "1",
                "-q:v", "2", // Qualité de l'image (1-31, 1 étant la meilleure)
                "-y", // Overwrite output file
                outputImage.toString()
        );

        try {
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException("FFmpeg a échoué avec le code: " + exitCode);
            }

            if (!Files.exists(outputImage)) {
                throw new IOException("L'image extraite n'a pas été créée");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interruption pendant l'extraction d'image", e);
        }
    }

    private String encodeImageToBase64(Path imageFile) throws IOException {
        byte[] imageBytes = Files.readAllBytes(imageFile);
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private String formatKeyFramesResult(List<String> keyFrames, double duration) {
        JSONArray framesArray = new JSONArray();
        for (int i = 0; i < keyFrames.size(); i++) {
            JSONObject frame = new JSONObject();
            double timestamp = duration * (i + 1) / (KEYFRAMES_COUNT + 1);

            frame.put("timestamp", timestamp);
            frame.put("image", "data:image/jpeg;base64," + keyFrames.get(i));
            framesArray.put(frame);
        }

        JSONObject result = new JSONObject();
        result.put("duration", duration);
        result.put("keyframes", framesArray);

        return result.toString();
    }

    private void cleanTempDirectory(Path tempDir) {
        try {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            log.warn("Impossible de supprimer le fichier temporaire: {}", path, e);
                        }
                    });
        } catch (IOException e) {
            log.warn("Erreur lors du nettoyage du répertoire temporaire", e);
        }
    }
}