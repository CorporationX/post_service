package faang.school.postservice.service.amazonS3;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class CustomMultipartFile implements MultipartFile {

    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] content;

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @NonNull
    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @NonNull
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(@NonNull File dest) throws IOException {
        try (FileOutputStream out = new FileOutputStream(dest)) {
            out.write(content);
        }
    }
}
