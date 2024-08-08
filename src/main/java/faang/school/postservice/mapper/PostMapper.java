package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.CachedPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {LikeMapper.class, CommentMapper.class})
public interface PostMapper {
    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    @Mapping(source = "views", target = "viewsQuantity")
    @Mapping(expression = "java(post.getLikes() != null ? post.getLikes().size() : 0)", target = "likesQuantity")
    CachedPostDto toCachedPostDto(Post post);

    List<CachedPostDto> toCachedPostDtoList(List<Post> posts);
}