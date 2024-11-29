package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.LikeEvent;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.publisher.LikeEventPublisherImpl;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.service.cache.MultiGetCacheService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final MultiGetCacheService<Long, LikeDto> likeCacheService;
    private final UserServiceClient client;
    private final UserServiceClient userServiceClient;
    private final LikeEventPublisherImpl likeEventPublisher;
    private final EventPublisher<LikeEvent> eventForFeedPublisher;

    @Override
    public void publish(LikeEvent likeEvent) {
        likeEventPublisher.publishLikeEvent(likeEvent);
        eventForFeedPublisher.publish(likeEvent);
    }

    @Override
    public void addLikeToPost(LikeDto likeDto, long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("There is no such post"));
        Like like = likeMapper.toLike(likeDto);
        LikeEvent likeEvent = LikeEvent.builder()
                .postId(postId)
                .authorId(post.getAuthorId())
                .userId(like.getUserId())
                .createdAt(like.getCreatedAt())
                .build();
        validateLike(like, post);
        checkUser(like.getUserId());
        validatePostAndCommentLikes(post, like);
        like.setPost(post);
        likeRepository.save(like);
        publish(likeEvent);
    }

    @Override
    public void deleteLikeFromPost(LikeDto likeDto, long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("There is no such post"));
        Like like = likeMapper.toLike(likeDto);
        if (!post.getLikes().remove(like)) {
            throw new DataValidationException("Post is not liked");
        }
        likeRepository.delete(like);
        postRepository.save(post);
    }

    @Override
    public List<UserDto> getUsersLikedPost(long postId) {
        List<Long> userIds = likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();

        return getUsers(userIds);
    }

    @Override
    public List<UserDto> getUsersLikedComm(long postId) {
        List<Long> userIds = likeRepository.findByCommentId(postId).stream()
                .map(Like::getUserId)
                .toList();

        return getUsers(userIds);
    }

    @Override
    public void addLikeToComment(LikeDto likeDto, long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("There is no such comment"));
        Like like = likeMapper.toLike(likeDto);
        checkUser(like.getUserId());
        validateLike(like, comment.getPost());
        validatePostAndCommentLikes(comment.getPost(), like); //возможна рекурсия
        like.setComment(comment);
        likeRepository.save(like);
    }

    @Override
    public void deleteLikeFromComment(LikeDto likeDto, long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("There is no such comment"));
        Like like = likeMapper.toLike(likeDto);
        if (!comment.getLikes().remove(like)) {
            throw new DataValidationException("Comment is not liked");
        }
        likeRepository.delete(like);
        commentRepository.save(comment);
    }

    @Override
    public List<LikeDto> getLikesForPublishedPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("There is no such post"));
        if (post.isPublished()) {
            return postRepository.findById(postId)
                    .orElseThrow(() -> new DataValidationException("There is no such comment"))
                    .getLikes().stream().map(likeMapper::toLikeDto).toList();
        } else {
            throw new DataValidationException("Post is not published");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikeDto> getLikesForPublishedPostFromCacheOrDb(long postId) {
        List<LikeDto> likes = likeCacheService.getAll(postId);
        if (likes.isEmpty()) {
            return getLikesForPublishedPost(postId);
        }
        return likes;
    }

    private void checkUser(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new UserNotFoundException("There is no such user");
        }
    }

    private void validateLike(Like like, Post post) {
        if (isLikedByUser(like.getUserId(), post.getLikes())) {
            throw new DataValidationException("Post is already liked.");
        }
        for (Comment comment : post.getComments()) {
            if (isLikedByUser(like.getUserId(), comment.getLikes())) {
                throw new DataValidationException("Comment of post is already liked.");
            }
        }
    }

    private void validatePostAndCommentLikes(Post post, Like like) {
        if (isLikedById(like.getId(), post.getLikes())) {
            throw new DataValidationException("Post already liked");
        }
        for (Comment comment : post.getComments()) {
            if (isLikedById(like.getId(), comment.getLikes())) {
                throw new DataValidationException("Comment already liked");
            }
        }
    }

    private boolean isLikedByUser(Long userId, List<Like> likes) {
        return likes.stream().anyMatch(like -> like.getUserId().equals(userId));
    }

    private boolean isLikedById(Long likeId, List<Like> likes) {
        for (Like like : likes) {
            if (like.getId() == likeId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Отправляет запросы на получение списка UserDto по списку id
     * user_service по 100 штук, и возвращает полный список UserDto
     */
    private List<UserDto> getUsers(List<Long> userIds) {
        List<UserDto> response = new ArrayList<>();

        int remains = userIds.size() % 100;
        int whole = userIds.size() / 100;

        try {
            for (int i = 0; i < whole; i++) {
                response.addAll(client.getUsersByIds(userIds.subList(i * 100, i * 100 + 100)));
            }
            if (remains > 0) {
                response.addAll(client.getUsersByIds(userIds.subList(whole * 100, whole * 100 + remains)));
            }
        } catch (Exception e) {
            log.warn("request to user_service did not send");
        }

        return response;
    }
}