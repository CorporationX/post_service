package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostInRedis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PostMapper {

    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    @Mapping(source = "likes", target = "numberOfLikes", qualifiedByName = "converter")
    PostInRedis entityToPostInRedis(Post post);

    @Named("converter")
    default AtomicLong listToNumber(List<Like> likes) {
        return new AtomicLong(likes.size());
    }
}
