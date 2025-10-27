package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.List;

public record PostDto(
        @Min(1)
        @JsonProperty("id") Long id,
        @JsonProperty("content") String content,
        @JsonProperty("author_id") Long authorId,
        @JsonProperty("project_id") Long projectId,
        @JsonProperty("like_ids") List<Long> likeIds,
        @JsonProperty("comment_ids") List<Long> commentIds,
        @JsonProperty("album_ids") List<Long> albumIds,
        @JsonProperty("ad_id") Long adId,
        @JsonProperty("resource_keys") List<String> resourceKeys,
        @JsonProperty("published") boolean published,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        @JsonProperty("published_at") LocalDateTime publishedAt,
        @JsonProperty("deleted") boolean deleted,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        @JsonProperty("created_at") LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        @JsonProperty("updated_at") LocalDateTime updatedAt
) {
}
