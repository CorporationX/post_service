package faang.school.postservice.service;

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
import faang.school.postservice.publisher.LikeEventPublisherImpl;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final UserServiceClient client;
    private final UserServiceClient userServiceClient;
    private final LikeEventPublisherImpl likeEventPublisher;

    @Override
    public void publish(LikeEvent likeEvent) {
        likeEventPublisher.publishLikeEvent(likeEvent);
    }

    @Override
    public void addLikeToPost(@Valid LikeDto likeDto, long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("There is no such post"));
        Like like = likeMapper.toLike(likeDto);
        validateLike(like, post);
        checkUser(like.getUserId());
        validatePostAndCommentLikes(post, like);
        like.setPost(post);
        likeRepository.save(like);
    }

    @Override
    public void deleteLikeFromPost(@Valid LikeDto likeDto, long postId) {
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
    public void addLikeToComment(@Valid LikeDto likeDto, long commentId) {
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
    public void deleteLikeFromComment(@Valid LikeDto likeDto, long commentId) {
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
    public List<LikeDto> findLikesOfPublishedPost(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new DataValidationException("There is no such post"));
        if (post.isPublished()) {
            return postRepository.findById(postId)
                    .orElseThrow(() -> new DataValidationException("There is no such comment"))
                    .getLikes().stream().map(likeMapper::toLikeDto).toList();
        } else {
            throw new DataValidationException("Post is not published");
        }
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