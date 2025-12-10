package faang.school.postservice.mapper;

import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.dto.post.PostDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PostMapper {
    Post postDraftDtoToPost(PostDraftDto postDraftDto);
    PostDraftDto postToPostDraftDto(Post post);
    Post postDtoToPost(PostDto postDto);
    PostDto postToPostDto(Post post);
    PostEvent postToPostEvent(Post post);
}
