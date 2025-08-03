package faang.school.postservice.mapper;


import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KafkaCommentEventMapper {

    @Mapping(target = "postId", source = "comment", qualifiedByName = "getPostIdFromComment")
    KafkaCommentEventDto toKafkaCommentEventDto(Comment comment);

    @Named("getPostIdFromComment")
    default long getPostId(Comment comment) {
        if (comment == null || comment.getPost() == null) {
            return 0L;
        }
        return comment.getPost().getId();
    }
}
