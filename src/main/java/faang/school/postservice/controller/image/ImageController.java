package faang.school.postservice.controller.image;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.postImage.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * ImageController — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author Пользователь
 * @since 13.08.2025
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final S3Service s3Service;
    private final UserContext userContext;
    private final PostRepository postRepository;

    @PostMapping("/{postId}/image")
    public ResponseEntity<List<Resource>> upload(@PathVariable Long postId,
                                                 @RequestParam("file") List<MultipartFile> files) {
        try {
            return ResponseEntity.ok(s3Service.uploadsFiles(postId, files));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam Long postId, @RequestParam String key) {
        log.info("Получено изображение {}", key);
        return s3Service.getFile(postId, key);
    }

    @DeleteMapping("/{postId}/key")
    public ResponseEntity<Void> removeImage(@PathVariable Long postId, @RequestParam String key) {
        s3Service.deleteFile(key);
        return ResponseEntity.noContent().build();
    }
}

