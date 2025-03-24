package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "albums", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "scheduledAt", ignore = true)
    Post toEntity(PostDto postDTO);

    @Mapping(source = "likes", target = "numberOfLikes", qualifiedByName = "convertLikesToNumberOfLikes")
    PostDto toDto(Post post);

    @Mapping(source = "likes", target = "numberOfLikes", qualifiedByName = "convertLikesToNumberOfLikes")
    List<PostDto> toDtoList(List<Post> post);

    @Named("convertLikesToNumberOfLikes")
    default long convertLikesToNumberOfLikes(List<Like> likes) {
        if (likes != null) {
            return likes.size();
        }
        return 0;
    }
}
