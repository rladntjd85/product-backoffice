package com.rladntjd85.backoffice.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Component
public class FileStorage {

    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_MIME = Set.of("image/jpeg", "image/png", "image/webp");

    private final Path rootDir;

    public FileStorage(@Value("${app.upload.root-dir}") String rootDir) {
        this.rootDir = Paths.get(rootDir).toAbsolutePath().normalize();
    }

    public StoredFile storeProductThumb(MultipartFile file) {
        return store(file, "products", "thumb_");
    }

    public StoredFile storeProductDetail(MultipartFile file) {
        return store(file, "products", "detail_");
    }

    public StoredFile store(MultipartFile file, String subDir, String prefix) {
        if (file == null || file.isEmpty()) return null;

        validate(file);

        String originalName = file.getOriginalFilename();
        String ext = extractExt(originalName);

        String dateDir = LocalDate.now().toString(); // yyyy-MM-dd
        String savedName = prefix + UUID.randomUUID().toString().replace("-", "") + "." + ext;

        Path targetDir = rootDir.resolve(subDir).resolve(dateDir).normalize();
        Path targetFile = targetDir.resolve(savedName).normalize();

        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetFile.toFile());
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 저장에 실패했습니다.");
        }

        String publicUrl = "/uploads/" + subDir + "/" + dateDir + "/" + savedName;
        return new StoredFile(publicUrl, originalName);
    }

    public void deleteByUrl(String url) {
        if (url == null || url.isBlank()) return;
        if (!url.startsWith("/uploads/")) return;

        String relative = url.substring("/uploads/".length()); // products/...
        Path filePath = rootDir.resolve(relative).normalize();
        if (!filePath.startsWith(rootDir)) return;

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
        }
    }

    private void validate(MultipartFile file) {
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("파일 용량은 5MB 이하여야 합니다.");
        }

        String ext = extractExt(file.getOriginalFilename());
        if (ext.isEmpty() || !ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("이미지는 jpg/jpeg/png/webp만 업로드할 수 있습니다.");
        }

        String mime = file.getContentType();
        if (mime == null || !ALLOWED_MIME.contains(mime)) {
            throw new IllegalArgumentException("이미지 MIME 타입이 올바르지 않습니다.");
        }
    }

    private String extractExt(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) return "";
        return filename.substring(idx + 1).toLowerCase();
    }

    public record StoredFile(String url, String originalName) {
    }
}