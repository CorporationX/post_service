package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.cache.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = { UserMapper.class },
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    PostDto toPostDto(faang.school.postservice.model.Post dbPost);

    PostDto toPostDto(Post cachePost);
}
