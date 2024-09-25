package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentCache;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheCommentMapper {
    public List<CommentCache> convertCommentsToCacheComments(List<Comment> comments) {
        return comments.stream().map(comment ->
                CommentCache.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .authorId(comment.getAuthorId())
                        .build()
        ).toList();
    }

    public List<CommentCache> ConvertCommentDtosToCacheComments(List<CommentDto> commentDtos) {
        return  commentDtos.stream().map(commentDto ->
                CommentCache.builder()
                        .id(commentDto.getId())
                        .content(commentDto.getContent())
                        .authorId(commentDto.getAuthorId())
                        .build()).toList();
    }
}
