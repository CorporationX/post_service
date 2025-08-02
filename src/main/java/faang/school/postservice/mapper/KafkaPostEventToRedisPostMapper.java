package faang.school.postservice.mapper;

import faang.school.postservice.dto.kafka.KafkaPostEventDto;
import faang.school.postservice.dto.redis.RedisPostDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KafkaPostEventToRedisPostMapper {

    RedisPostDto postEventToRedisPostDto(KafkaPostEventDto postEvent);
}
