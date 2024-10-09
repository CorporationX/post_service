package faang.school.postservice.mapper.user;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.redis.UserRedis;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserRedis toRedisModel(UserDto dto);

    UserRedis toRedisModels(UserDto dto);

}
