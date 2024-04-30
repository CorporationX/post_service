package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostViewDto;
import faang.school.postservice.model.PostView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * @author Alexander Bulgakov
 */

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostViewMapper {

    @Mapping(source = "post.id", target = "postId")
    PostViewDto toDto(PostView entity);

    @Mapping(target = "post", ignore = true)
    PostView toEntity(PostViewDto dto);
}
