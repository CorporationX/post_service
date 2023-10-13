package faang.school.postservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class FileConverter {
    public Optional<File> convert(byte[] bytes, String fileName) {
        File convertFile = new File(fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(convertFile)) {
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            log.error("Error converting multipartFile to file ", e);
            return Optional.empty();
        }
        return Optional.of(convertFile);
    }
}
