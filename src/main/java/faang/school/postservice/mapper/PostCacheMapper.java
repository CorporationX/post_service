package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.redisModel.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostCacheMapper {
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "postId", source = "project.id")
    @Mapping(target = "likeCount", expression = "java(mapLatestComments(post.getComments()))")
    PostCache toCache(Post post);

    Post toEntity(PostCache postCache);

    @Named("mapLatestComments")
    default List<CommentDto> mapLatestComments(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .limit(3)
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());
    }

    default CommentDto convertToCommentDto(Comment comment) {
        CommentDto dto = CommentDto.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .postId(comment.getPost().getId())
                .authorId(comment.getAuthorId())
                .content(comment.getContent())
                .build();
        return dto;
    }
}
