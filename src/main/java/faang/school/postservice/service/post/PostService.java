package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;

import java.util.List;

public interface PostService {
    PostDto createDraft(CreatePostDto postDto);

    PostDto publishPost(Long postId);

    PostDto updatePost(Long postId, CreatePostDto postDto);

    PostDto softDeletePost(Long postId);

    PostDto getPostById(Long postId);

    List<PostDto> getDraftsByUser(Long userId);

    List<PostDto> getDraftsByProject(Long projectId);

    List<PostDto> getPublishedByUser(Long userId);

    List<PostDto> getPublishedByProject(Long projectId);

    void publishScheduledPosts();
}
