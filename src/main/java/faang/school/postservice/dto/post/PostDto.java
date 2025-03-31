package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonSerialize
public record PostDto(

        @JsonProperty("id")
        Long id,

        @JsonProperty("content")
        @NotBlank(message = "Post must contain content")
        String content,

        @JsonProperty("authorId")
        Long authorId,

        @JsonProperty("projectId")
        Long projectId,

        @JsonProperty("numberOfLikes")
        long numberOfLikes,

        @JsonProperty("published")
        boolean published,

        @JsonProperty("deleted")
        boolean deleted,

        @JsonProperty("publishedAt")
        LocalDateTime publishedAt,

        @JsonProperty("createdAt")
        LocalDateTime createdAt,

        @JsonProperty("updatedAt")
        LocalDateTime updatedAt) {

    public PostDto() {
        this(
                null,
                "",
                null,
                null,
                0L,
                false,
                false,
                null,
                null,
                null
        );
    }
}
