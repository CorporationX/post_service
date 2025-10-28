package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;

import java.util.Optional;

public interface CommentMapper {

    static CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        Long postId = Optional.ofNullable(comment.getPost())
                .map(Post::getId)
                .orElse(null);

        return CommentDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .authorId(comment.getAuthorId())
                .content(comment.getContent())
                .largeImageFileKey(comment.getLargeImageFileKey())
                .smallImageFileKey(comment.getSmallImageFileKey())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    static Comment toEntity(CommentCreateDto dto, Post post, Long authorId) {
        if (dto == null || post == null || authorId == null) {
            return null;
        }
        return Comment.builder()
                .content(Optional.ofNullable(dto.content()).orElse(""))
                .authorId(authorId)
                .post(post)
                .largeImageFileKey(Optional.ofNullable(dto.largeImageFileKey()).orElse(""))
                .smallImageFileKey(Optional.ofNullable(dto.smallImageFileKey()).orElse(""))
                .build();
    }
}
