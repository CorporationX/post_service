package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;

import java.util.List;

public interface PostService {
    PostDto getPostById(long postId);

    List<PostDto> getNotDeletedUserDrafts(long userId);

    List<PostDto> getNotDeletedProjectDrafts(long projectId);

    List<PostDto> getNotDeletedUserPublished(long userId);

    List<PostDto> getNotDeletedProjectPublished(long projectId);

    PostDto deletePost(long postId);

    PostDto createPost(PostDto postDto);

    PostDto publishPost(long postId);

    PostDto updatePost(long postId, PostUpdateDto postUpdateDto);
}