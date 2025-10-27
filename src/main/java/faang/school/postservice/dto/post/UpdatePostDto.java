package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.List;

public record UpdatePostDto(
        @Min(1)
        @JsonProperty("id") Long id,
        @JsonProperty("content") String content,
        @JsonProperty("project_id") Long projectId,
        @JsonProperty("album_ids") List<Long> albumIds,
        @JsonProperty("ad_id") Long adId,
        @JsonProperty("resourse_keys") List<String> resourceKeys,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        @JsonProperty("scheduled_at") LocalDateTime scheduledAt
) {
}
