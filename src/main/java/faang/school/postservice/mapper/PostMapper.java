package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "likesToLikesCount")
    PostDto toDto(Post entity);

    Post toEntity(PostDto dto);

    @Named("likesToLikesCount")
    default Long likesToLikesCount(List<Like> likes) {
        if (likes == null) return 0L;
        return ((long) likes.size());
    }
}