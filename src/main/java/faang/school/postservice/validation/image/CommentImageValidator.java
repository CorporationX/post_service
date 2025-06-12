package faang.school.postservice.validation.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import faang.school.postservice.exception.file.FileTooLargeException;
import faang.school.postservice.exception.file.UnsupportedFileTypeException;

import java.util.Set;

@Component
public class CommentImageValidator {

    @Value("${file.upload.max-size}")
    private long maxSize;

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
            throw new UnsupportedFileTypeException("Файл пуст или не выбран.");
        }

        if (file.getSize() > maxSize) {
            throw new FileTooLargeException("Файл слишком большой. Максимальный размер: " + maxSize);
        }

        String contentType = file.getContentType();
        if (contentType == null || !isSupportedMediaType(contentType)) {
            throw new UnsupportedFileTypeException("Неподдерживаемый тип файла: " + contentType);
        }

        String filename = file.getOriginalFilename();
        if (!hasExtension(filename)) {
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
