package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikeDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id;

        @NotEmpty(message = "Content cannot be empty")
        String content;

        @Min(value = 1L, message = "Id cannot be lower than 1")
        Long authorId;

        @Min(value = 1L, message = "Id cannot be lower than 1")
        Long projectId;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        List<LikeDto> likes;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        List<CommentDto> comments;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        List<AlbumDto> albums;

        @Min(value = 1L, message = "Id cannot be lower than 1")
        @NotEmpty(message = "AdId cannot be empty")
        Long adId;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime publishedAt;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime scheduledAt;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        boolean published;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        boolean deleted;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime createdAt;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime updatedAt;
}
