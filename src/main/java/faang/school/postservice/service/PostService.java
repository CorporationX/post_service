package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostCommentEvent;
import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;

import java.util.List;

public interface PostService {
    PostResponseDto createPostDraft(PostCreateRequestDto postCreateRequestDto);

    PostResponseDto publishPostDraft(Long postId);

    void publishScheduledPosts();

    PostResponseDto updatePost(Long postId, PostUpdateRequestDto postUpdateRequestDto);

    void deletePost(Long postId);

    PostResponseDto getPostWithCache(Long postId);

    void incrementPostLikesCounter(long postId);

    void decrementPostLikesCounter(long postId);

    void addCommentToHash(long postId, PostCommentEvent postCommentEvent);

    List<PostResponseDto> findAllByFilter(PostFilterDto filter);

    List<PostResponseDto> getPostsByUser(long userId);
}
