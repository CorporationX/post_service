package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {LikeMapper.class, CommentMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "likes", source = "likes")
    @Mapping(target = "comments", source = "comments")
    PostDto toDto(Post post);

    @Mapping(target = "likes", source = "likes")
    @Mapping(target = "comments", source = "comments")
    Post toEntity(PostDto postDto);
}
