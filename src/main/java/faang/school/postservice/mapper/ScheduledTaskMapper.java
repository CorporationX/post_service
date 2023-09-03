package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.model.scheduled.ScheduledTask;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ScheduledTaskMapper {

    ScheduledTaskDto toDto(ScheduledTask entity);

    ScheduledTask toEntity(ScheduledTaskDto dto);
}
