package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    @Mapping(target = "post", source = "post")
    @Mapping(target = "id", source = "commentDto.id")
    @Mapping(target = "content", source = "commentDto.content")
    @Mapping(target = "authorId", source = "commentDto.authorId")
    @Mapping(target = "createdAt", source = "commentDto.createdAt")
    Comment toEntity(CommentDto commentDto, Post post);
}
