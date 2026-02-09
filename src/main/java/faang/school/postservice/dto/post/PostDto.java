package faang.school.postservice.dto.post;

import faang.school.postservice.model.Like;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ad.Ad;

import java.time.LocalDateTime;
import java.util.List;

public record PostDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        List<Like> likes,
        List<Comment> comments,
        List<Album> albums,
        Ad ad,
        List<Resource> resources,
        boolean published,
        boolean deleted,
        LocalDateTime publisedAt,
        LocalDateTime scheduledAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}
