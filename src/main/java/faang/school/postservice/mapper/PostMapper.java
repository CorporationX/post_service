package faang.school.postservice.mapper;

import faang.school.postservice.dto.event.NewsFeedEventDto;
import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.NewsFeedRedisEntity;
import faang.school.postservice.model.redis.PostRedisEntity;
import faang.school.postservice.model.redis.UserRedisEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface PostMapper {

    @Mapping(target = "published", constant = "false")
    @Mapping(target = "deleted", constant = "false")
    Post toCreateEntity(PostCreateDto postDto);

    PostDto toDto(Post post);

    @Mapping(target = "timeStamp", expression = "java(getTimeStamp(post))")
    @Mapping(target = "postId", source = "post.id")
    PostEventDto toEventDto(Post post);

    @Mapping(target = "followersId", source = "followersId")
    NewsFeedEventDto toNewsFeedEventDto(PostEventDto postEventDto, List<Long> followersId);

    @Mapping(target = "userId", source = "postEventDto.authorId")
    UserRedisEntity toUserRedisEntity(PostEventDto postEventDto);

    PostRedisEntity toPostRedisEntity(PostEventDto postEventDto);

    @Mapping(target = "userId", source = "followerId")
    NewsFeedRedisEntity toNewsFeedRedisEntity(NewsFeedEventDto newsFeedEventDto, Long followerId);

    @Named(value = "getTimeStamp")
    default Long getTimeStamp(Post post) {
        ZonedDateTime zonedDateTime = post.getCreatedAt().atZone(ZoneId.systemDefault());
        // Получение значения Instant
        Instant instant = zonedDateTime.toInstant();
        // Преобразование в миллисекунды с начала эпохи Unix
        return instant.toEpochMilli();
    }
}
