package faang.school.postservice.service.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomMultipartFile implements MultipartFile {
    private byte[] fileContent;
    private String name;
    private String originalFileName;
    private String contentType;

    @Override
    public String getName() {
        return this.name;
    };

    @Override
    @Nullable
    public String getOriginalFilename() {
        return this.originalFileName;
    }

    @Override
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return this.fileContent == null || this.fileContent.length == 0;
    }

    @Override
    public long getSize() {
        return this.fileContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.fileContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileContent);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
        try (InputStream in = getInputStream()) {
            java.nio.file.Files.copy(in, dest.toPath());
        }
    }
}
