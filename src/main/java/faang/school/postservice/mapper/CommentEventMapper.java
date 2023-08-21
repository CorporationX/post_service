package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentEventMapper {
    CommentEventDto toDto(Long send);
}
