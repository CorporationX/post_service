package faang.school.postservice.dto.post;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDto(
        String content,
        UserDto authorDto,
        ProjectDto projectDto,
        List<LikeDto> likesDto,
        List<CommentDto> commentsDto,
        List<AlbumDto> albumsDto,
        boolean published,
        LocalDateTime publishedAt,
        LocalDateTime scheduledAt,
        boolean deleted
) {
}
