package faang.school.postservice.dto.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class S3Dto {
    private Resource resource;
    private String name;
    private String contentType;
    private long contentLength;
}
