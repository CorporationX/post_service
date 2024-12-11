package faang.school.postservice.mapper;


import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toPost(PostDto postDto);

    PostDto toPostDto(Post post);
}
