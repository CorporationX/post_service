package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.dto.post.PostUpdateDto;

import java.util.List;

public interface PostService {
    PostOutputDto getPostById(long postId);

    List<PostOutputDto> getNotDeletedUserDrafts(long userId);

    List<PostOutputDto> getNotDeletedProjectDrafts(long projectId);

    List<PostOutputDto> getNotDeletedUserPublished(long userId);

    List<PostOutputDto> getNotDeletedProjectPublished(long projectId);

    PostOutputDto deletePost(long postId);

    PostOutputDto createPost(PostCreateDto postCreateDto);

    PostOutputDto publishPost(long postId);

    PostOutputDto updatePost(long postId, PostUpdateDto postUpdateDto);

    void publishScheduledPosts();
}