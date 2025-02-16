package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeValidationService likeValidationService;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;

    @Transactional(readOnly = true)
    public List<UserDto> getUsersWhoLikedPost(Long postId) {
        List<Long> userIds = likeRepository.findByPostId(postId)
                .stream()
                .map(Like::getUserId)
                .collect(Collectors.toList());
        return fetchUsersInBatches(userIds);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersWhoLikedComment(Long commentId) {
        List<Long> userIds = likeRepository.findByCommentId(commentId)
                .stream()
                .map(Like::getUserId)
                .collect(Collectors.toList());
        return fetchUsersInBatches(userIds);
    }

    @Transactional
    public void addLikeToPost(Long postId, Long commentId, Long currentUserId) {
        try {
            userServiceClient.getUser(currentUserId);
        } catch (FeignException.NotFound ex) {
            throw new UserNotFoundException("User not found with id: " + currentUserId);
        }

        likeValidationService.validatePostAlreadyLiked(currentUserId, postId);
        likeValidationService.validateLikeTarget(postId, commentId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
        Like like = Like.builder()
                .userId(currentUserId)
                .post(post)
                .build();

        post.getLikes().add(like);
        likeRepository.save(like);
        postRepository.save(post);
    }

    @Transactional
    public void removeLikeFromPost(Long postId, Long currentUserId) {
        likeValidationService.validatePostNotBeenLiked(currentUserId, postId);
        likeRepository.deleteByUserIdAndPostId(currentUserId, postId);
    }

    @Transactional
    public void addLikeToComment(Long commentId, Long postId, Long currentUserId) {
        try {
            userServiceClient.getUser(currentUserId);
        } catch (FeignException.NotFound ex) {
            throw new UserNotFoundException("User not found with id: " + currentUserId);
        }

        likeValidationService.validateCommentAlreadyLiked(currentUserId, commentId);
        likeValidationService.validateLikeTarget(postId, commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        Like like = Like.builder()
                .userId(currentUserId)
                .comment(comment)
                .build();

        comment.getLikes().add(like);
        likeRepository.save(like);
        commentRepository.save(comment);
    }

    @Transactional
    public void removeLikeFromComment(Long commentId, Long currentUserId) {
        likeValidationService.validateCommentNotBeenLiked(currentUserId, commentId);
        likeRepository.deleteByUserIdAndCommentId(currentUserId, commentId);
    }

    private List<UserDto> fetchUsersInBatches(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        int batchSize = 100;
        int page = 0;
        List<UserDto> allUsers = new ArrayList<>();

        while (true) {
            Pageable pageable = PageRequest.of(page, batchSize);
            Page<UserDto> userPage = userServiceClient.getUsersByIds(userIds, pageable);
            allUsers.addAll(userPage.getContent());
            if (userPage.isLast()) {
                break;
            }
            page++;
        }
        return allUsers;
    }
}