package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record CreatePostDto(
        @NotBlank
        @JsonProperty("content") String content,
        @NotNull
        @JsonProperty("project_id") Long projectId,
        @NotNull
        @JsonProperty("album_ids") List<Long> albumIds,
        @NotNull
        @JsonProperty("ad_id") Long adId,
        @NotNull
        @JsonProperty("resource_keys") List<String> resourceKeys,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        @JsonProperty("schedule_at") LocalDateTime scheduledAt
) {
}
