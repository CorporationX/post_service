package faang.school.postservice.validation.image;

import faang.school.postservice.config.file.FileProperties;
import faang.school.postservice.exception.file.FileTooLargeException;
import faang.school.postservice.exception.file.UnsupportedFileTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageValidator {

    private final FileProperties fileProperties;

    private static final Set<MediaType> SUPPORTED_IMAGE_TYPES = Set.of(
            MediaType.IMAGE_JPEG,
            MediaType.IMAGE_PNG,
            MediaType.IMAGE_GIF,
            MediaType.valueOf("image/bmp"),
            MediaType.valueOf("image/webp"),
            MediaType.valueOf("image/x-windows-bmp")
    );

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("Файл пуст или не выбран.");
            throw new UnsupportedFileTypeException("Файл пуст или не выбран.");
        }

        log.debug("Получен файл: name={}, size={}, type={}",
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        if (file.getSize() > fileProperties.getMaxSize()) {
            log.warn("Размер файла превышает допустимый предел: {} > {}", file.getSize(), fileProperties.getMaxSize());
            throw new FileTooLargeException("Файл слишком большой. Максимальный размер: " + fileProperties.getMaxSize());
        }

        String contentType = file.getContentType();
        if (contentType == null || !isSupportedMediaType(contentType)) {
            log.warn("Неподдерживаемый тип файла: {}", contentType);
            throw new UnsupportedFileTypeException("Неподдерживаемый тип файла: " + contentType);
        }

        String filename = file.getOriginalFilename();
        if (!hasExtension(filename)) {
            log.warn("Файл не содержит расширения: {}", filename);
            throw new UnsupportedFileTypeException("Файл должен иметь расширение.");
        }
    }

    private boolean isSupportedMediaType(String contentType) {
        try {
            MediaType mediaType = MediaType.parseMediaType(contentType);
            return SUPPORTED_IMAGE_TYPES.contains(mediaType);
        } catch (InvalidMediaTypeException e) {
            return false;
        }
    }

    private boolean hasExtension(String fileName) {
        return fileName != null && fileName.contains(".") && !fileName.endsWith(".");
    }
}
