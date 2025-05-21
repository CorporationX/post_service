package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;

import java.util.List;

public interface PostService {
    PostDto getPostById(Long postId);

    List<PostDto> getNotDeletedUserDrafts(Long userId);

    List<PostDto> getNotDeletedProjectDrafts(Long projectId);

    List<PostDto> getNotDeletedUserPublished(Long userId);

    List<PostDto> getNotDeletedProjectPublished(Long projectId);

    PostDto deletePost(Long postId);

    PostDto createPost(PostDto postDto);

    PostDto publishPost(Long postId);

    PostDto updatePost(Long postId, PostUpdateDto postUpdateDto);
}