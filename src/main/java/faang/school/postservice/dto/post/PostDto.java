package faang.school.postservice.dto.post;

import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ad.Ad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    Long id;
    String content;
    Long authorId;
    Long projectId;
    List<Like> likes;
    List<Comment> comments;
    List<Album> albums;
    Ad ad;
    List<Resource> resources;
    boolean published;
    LocalDateTime publishedAt;
    LocalDateTime scheduledAt;
    boolean deleted;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}
