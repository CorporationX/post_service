package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

import java.util.List;

public interface PostService {

    PostDto createDraft(PostDto postDto);

    PostDto publishPost(Long postId);

    PostDto updatePost(Long postId, PostDto postDto);

    void deletePost(Long postId);

    PostDto getPost(Long postId);

    List<PostDto> getAllDraftsByAuthorId(Long userId);

    List<PostDto> getAllDraftsByProjectId(Long projectId);

    List<PostDto> getAllPostsByAuthorId(Long authorId);

    List<PostDto> getAllPostsByProjectId(Long projectId);

    Post getExistingPost(Long postId);
}
