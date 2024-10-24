package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.request.PostCreationRequest;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toPostFromCreationRequest(PostCreationRequest postCreationRequest);

    @Mapping(source = "likes", target = "likes", qualifiedByName = "getLikesCount")
    PostDto toPostDto(Post post);

    List<PostDto> toPostDtoList(List<Post> posts);

    @Mapping(source = "id", target = "postId")
    PostViewEvent toPostViewEvent(Post post);

    @Named("getLikesCount")
    default int getLikesCount(List<Like> likes) {
        return likes.size();
    }
}