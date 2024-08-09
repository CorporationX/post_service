package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(source = "likes", target = "likes", ignore = true)
    Post toEntity(PostDto dto);

    @Mapping(target = "likes", source = "likes", qualifiedByName = "sizeToLong")
    PostDto toDto(Post post);

    @Named("sizeToLong")
    default Long sizeToLong(List<?> list) {
        if (list == null) {
            return 0L;
        }
        return (long) list.size();
    }
}
