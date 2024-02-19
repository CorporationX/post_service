package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;

import java.util.List;

public interface PostService {
    void createDraft(PostDto postDto);
    void publish(long id);
    void update(PostDto postDto);
    void removeSoftly(long id);
    PostDto getPostById(long id);
    List<PostDto> getDraftsByAuthorId(long id);
    List<PostDto> getDraftsByProjectId(long id);
    List<PostDto> getPublishedPostsByAuthorId(long id);
    List<PostDto> getPublishedPostsByProjectId(long id);
}
