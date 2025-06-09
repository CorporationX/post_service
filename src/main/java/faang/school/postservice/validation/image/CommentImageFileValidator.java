package faang.school.postservice.validation.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import faang.school.postservice.exception.file.FileTooLargeException;
import faang.school.postservice.exception.file.UnsupportedFileTypeException;

import java.util.Objects;
import java.util.Set;

@Component
public class CommentImageFileValidator {

    @Value("${file.upload.max-size}")
    private int maxSize;

    private static final Set<String> IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".wbmp");

    public void validate(MultipartFile file) {
        if (file.getSize() > maxSize) {
            throw new FileTooLargeException("Файл слишком большой. Максимальный размер: " + maxSize);
        }

        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new UnsupportedFileTypeException("Доступны только изображения.");
        }


        String lowerCaseName = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
        int index = lowerCaseName.lastIndexOf(".");
        if (index == -1 || index == lowerCaseName.length() - 1) {
            throw new UnsupportedFileTypeException("Файл без расширения или с некорректным расширением.");
        }

        String fileExtension = lowerCaseName.substring(index);
        if (!IMAGE_EXTENSIONS.contains(fileExtension)) {
            throw new UnsupportedFileTypeException("Доступны только изображения с заданными форматами.");
        }
    }
}
