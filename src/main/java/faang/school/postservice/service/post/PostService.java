package faang.school.postservice.service.post;

import faang.school.postservice.model.dto.post.PostDto;

import java.util.List;

public interface PostService {

    List<PostDto> getPostsByHashtag(String hashtag);

    PostDto createDraftPost(PostDto postDto);

    PostDto publishPost(PostDto postDto);

    PostDto updatePost(PostDto postDto);

    PostDto softDeletePost(Long postId);

    PostDto getPost(Long id);

    List<PostDto> getAllDraftsByAuthorId(Long userId);

    List<PostDto> getAllDraftsByProjectId(Long projectId);

    List<PostDto> getAllPublishedPostsByAuthorId(Long userId);

    List<PostDto> getAllPublishedPostsByProjectId(Long projectId);
}
