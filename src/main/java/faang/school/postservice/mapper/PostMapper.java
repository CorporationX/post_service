package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;


@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "likesAmount", source = "likes", qualifiedByName = "mapLikesAmountToDto")
    PostDto toDto(Post post);

    Post toPost(PostDto postDto);

    Post toPost(PostCreateDto postCreateDto);

    Post update(PostDto postDto);

    @Named("mapLikesAmountToDto")
    default Long mapLikesAmountToDto(List<Like> likes) {
        return (long) likes.size();
    }
}
