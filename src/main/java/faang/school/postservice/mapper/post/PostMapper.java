package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Post;
import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CommentMapper.class,
    builder = @Builder(disableBuilder = true))
public interface PostMapper {

    Post toEntity(PostDto postDto);

    @Mapping(target = "numberLikes", ignore = true)
    PostDto toDto(Post post);

    PostEvent toPostEvent(Post post);

    @AfterMapping
    default void updateFields(Post post, @MappingTarget PostDto target) {
        long likesCount = Optional.ofNullable(post.getLikes())
            .stream()
            .count();
        target.setNumberLikes(likesCount);
    }
}