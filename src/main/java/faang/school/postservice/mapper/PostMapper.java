package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE,
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostMapper {

    Post toPost(PostCreateDraftDto postCreateDraftDto);

    PostDto toPostDto(Post post);
}
