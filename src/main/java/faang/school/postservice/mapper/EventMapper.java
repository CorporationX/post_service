package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;

import faang.school.postservice.producer.event.PostLikeKafkaEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    PostLikeKafkaEvent toPostLikeKafkaEvent(LikeDto dto);
}
