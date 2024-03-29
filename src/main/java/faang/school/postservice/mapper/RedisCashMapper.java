package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.model.redis.UserCache;
import faang.school.postservice.service.RedisCashService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisCashMapper {

//    @Mapping(target = "postId", source = "id")
    PostCache toCash(Post post);

    UserCache toCash(UserDto userDto);

    UserDto toDto(UserCache userCache);

    PostFeedDto toPostFeedDto(PostCache postCache);

    PostFeedDto toPostFeedDto(Post post);

}