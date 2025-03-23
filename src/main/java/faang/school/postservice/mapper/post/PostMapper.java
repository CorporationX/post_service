package faang.school.postservice.mapper.post;


import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "likes", ignore = true)
    Post toEntity(PostDto postDto);

    @Mapping(target = "likes", source = "post", qualifiedByName = "countLikes")
    PostDto toDto(Post post);

    List<Post> toEntities(List<PostDto> postDtos);

    List<PostDto> toDtos(List<Post> posts);

    @Named("countLikes")
    static long countLikes(Post post) {
        return post.getLikes() != null ? post.getLikes().size() : 0;
    }
}