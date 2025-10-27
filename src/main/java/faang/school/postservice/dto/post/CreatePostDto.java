package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public record CreatePostDto(
        @NotBlank
        @JsonProperty("content") String content,
        @JsonProperty("project_id") Long projectId,
        @JsonProperty("album_ids") List<Long> albumIds,
        @JsonProperty("ad_id") Long adId,
        @JsonProperty("resource_keys") List<String> resourceKeys,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        @JsonProperty("schedule_at") LocalDateTime scheduledAt
) {
}
