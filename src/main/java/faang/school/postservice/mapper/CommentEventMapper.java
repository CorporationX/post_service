package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentEvent;
import faang.school.postservice.dto.comment.CommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentEventMapper {
    CommentEvent toEvent(CommentDto commentDto);
}
