package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "postId", target = "post.id")
    Comment toComment(CommentDto commentDto);

    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDto(List<Comment> comments);

    @Mapping(target = "commentContent", source = "content")
    FeedEventProto.FeedEvent toProto(CommentDto commentDto);

    @Mapping(target = "content", source = "commentContent")
    CommentDto toDto(FeedEventProto.FeedEvent feedEvent);
}