package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;

import java.util.List;

public interface PostService {
    PostDto createDraft(PostDto postDto);
    PostDto publish(long id);
    PostDto update(PostDto postDto);
    PostDto deletePost(long id);
    PostDto getPostById(long id);
    List<PostDto> getDraftsByAuthorId(long id);
    List<PostDto> getDraftsByProjectId(long id);
    List<PostDto> getPostsByAuthorId(long id);
    List<PostDto> getPostsByProjectId(long id);
}
