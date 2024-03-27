package faang.school.postservice.mapper.news_feed;

import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.feed_dto.PostForFeedDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostForFeedMapper {
    @Mapping(target = "author", source = "userDto")
    @Mapping(target = "id", source = "postCache.id")
    PostForFeedDto toDto(PostCache postCache, UserDto userDto);

    @Mapping(target = "author", source = "userDto")
    @Mapping(target = "id", source = "post.id")
    PostForFeedDto toDto(Post post, UserDto userDto);
}
