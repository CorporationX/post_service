package faang.school.postservice.dto.s3;

import faang.school.postservice.model.Resource;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class S3Dto {
    private Resource resource;
    private String name;
    private long postId;
}
