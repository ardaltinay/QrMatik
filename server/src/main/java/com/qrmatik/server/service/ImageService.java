package com.qrmatik.server.service;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    public record SavedImages(String mediumUrl, String thumbUrl) {
    }

    public SavedImages saveMenuItemImage(String tenantCode, UUID menuItemId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty())
            throw new IOException("Empty file");
        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase(Locale.ROOT) : "";
        String originalName = file.getOriginalFilename() != null
                ? file.getOriginalFilename().toLowerCase(Locale.ROOT)
                : "";
        String ext = detectExt(originalName, contentType);
        if (ext == null) {
            throw new IOException("Unsupported image type. Please upload JPG/PNG (WEBP supported if enabled).");
        }
        Path base = resolveUploadBase();
        Path dir = base.resolve("tenants").resolve(safe(tenantCode)).resolve("menu")
                .resolve(String.valueOf(menuItemId));
        log.info("Saving menu image. uploadDir={}, resolvedBase={}, resolvedDir={}", uploadDir, base.toAbsolutePath(),
                dir.toAbsolutePath());
        Files.createDirectories(dir);

        Path original = dir.resolve("original." + ext);
        try (var in = file.getInputStream()) {
            Files.copy(in, original, StandardCopyOption.REPLACE_EXISTING);
        }

        Path medium = dir.resolve("medium.jpg");
        Thumbnails.of(original.toFile()).size(800, 800).outputFormat("jpg").toFile(medium.toFile());

        Path thumb = dir.resolve("thumb.jpg");
        Thumbnails.of(original.toFile()).size(200, 200).outputFormat("jpg").toFile(thumb.toFile());

        if (!Files.exists(medium) || !Files.exists(thumb)) {
            log.error("Image generation incomplete. Exists? medium={}, thumb={}", Files.exists(medium),
                    Files.exists(thumb));
        }

        String baseUrl = "/files/tenants/" + safe(tenantCode) + "/menu/" + String.valueOf(menuItemId) + "/";
        return new SavedImages(baseUrl + "medium.jpg", baseUrl + "thumb.jpg");
    }

    private Path resolveUploadBase() {
        try {
            Path configured = Paths.get(uploadDir);
            if (configured.isAbsolute())
                return configured.normalize();
            URI codeSrc = ImageService.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path loc = Paths.get(codeSrc);
            if (loc.getFileName() != null && loc.getFileName().toString().equals("classes")) {
                loc = loc.getParent();
            }
            if (loc.getFileName() != null && loc.getFileName().toString().equals("target")) {
                loc = loc.getParent();
            }
            Path moduleRoot = loc;
            return moduleRoot.resolve(uploadDir).toAbsolutePath().normalize();
        } catch (Exception e) {
            return Paths.get(uploadDir).toAbsolutePath().normalize();
        }
    }

    private String safe(String s) {
        if (s == null)
            return "default";
        return s.replaceAll("[^a-zA-Z0-9_-]", "-");
    }

    private String detectExt(String originalName, String contentType) {
        String ext = null;
        if (originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
            if (ext.equals("jpeg"))
                ext = "jpg";
            if (ext.equals("jpg") || ext.equals("png") || ext.equals("webp"))
                return ext;
        }
        if (contentType != null && contentType.startsWith("image/")) {
            if (contentType.contains("jpeg"))
                return "jpg";
            if (contentType.contains("png"))
                return "png";
            if (contentType.contains("webp"))
                return "webp";
        }
        return null;
    }
}
