package faang.school.postservice.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Builder
public record PostDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        Long adId,
        List<Long> resourcesId,
        List<String> hashtagsName
) {
}
