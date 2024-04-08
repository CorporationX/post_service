package faang.school.postservice.dto.post;

import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ad.Ad;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostDto {
    private Long id;
    @NotBlank(message = "Content is required")
    private String content;
    private Long authorId;
    private Long projectId;
    List<Like> likes;
    List<Comment> comments;
    List<Album> albums;
    Ad ad;
    List<Resource> resources;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    LocalDateTime createdAt;
    LocalDateTime scheduledAt;
    private boolean published;
    private boolean deleted;
    private Long likeCount;
}