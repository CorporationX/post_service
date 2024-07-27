package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDtoList(List<Comment> comments);

    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CommentDto commentDto);

//    private Long id;
//    private String content;
//    private Long authorId;
//    private Long postId;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;

//    private long id;
//    private String content;
//    private long authorId;
//    private List<Like> likes;
//    private Post post;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;

}
