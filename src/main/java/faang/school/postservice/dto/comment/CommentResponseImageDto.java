package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseImageDto {

    private Resource resource;
    private String fileName;
    private String contentType;
    private long contentLength;
}
