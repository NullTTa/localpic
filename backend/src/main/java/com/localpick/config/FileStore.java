package com.localpick.config;

import com.localpick.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Component
public class FileStore {

    private static final Set<String> ALLOWED = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @Value("${app.upload-dir}")
    private String uploadDir;

    /** 이미지를 저장하고 브라우저에서 접근 가능한 URL(/uploads/...)을 돌려준다. 파일이 없으면 null */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = original.contains(".")
                ? original.substring(original.lastIndexOf('.') + 1).toLowerCase()
                : "";
        if (!ALLOWED.contains(ext)) {
            throw BusinessException.badRequest("이미지 파일(jpg, png, gif, webp)만 올릴 수 있습니다.");
        }
        String saveName = UUID.randomUUID() + "." + ext;
        try {
            Path dir = Path.of(uploadDir).toAbsolutePath();
            Files.createDirectories(dir);
            file.transferTo(dir.resolve(saveName));
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장 실패", e);
        }
        return "/uploads/" + saveName;
    }
}
