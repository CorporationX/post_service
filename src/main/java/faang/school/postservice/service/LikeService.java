package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.messaging.likeevent.LikeEventPublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.exception.DataValidationException;
import faang.school.postservice.util.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;
    private final LikeEventPublisher likeEventPublisher;

    @Value("${batch-size-from-like}")
    private int BATCH_SIZE;

    public LikeDto createLikeOnPost(LikeDto likeDto) {
        isUserExist(likeDto);
        long postId = likeDto.getPostId();
        Optional<Like> byPostIdAndUserId = likeRepository.findByPostIdAndUserId(postId, likeDto.getUserId());
        if (byPostIdAndUserId.isEmpty()) {
            Like postLike = likeMapper.toEntity(likeDto);
            Optional<Post> postById = postRepository.findById(postId);
            postById.ifPresent(post -> {
                post.getLikes().add(postLike);
                postLike.setPost(post);
            });
            likeRepository.save(postLike);
            likeEventPublisher.publish(likeMapper.toEvent(byPostIdAndUserId.get()));
            return likeMapper.toDto(postLike);
        }
        likeEventPublisher.publish(likeMapper.toEvent(byPostIdAndUserId.get()));
        return likeMapper.toDto(byPostIdAndUserId.get());
    }

    public LikeDto createLikeOnComment(LikeDto likeDto) {
        isUserExist(likeDto);
        long commentId = likeDto.getCommentId();
        Optional<Like> byCommentIdAndUserId = likeRepository.findByCommentIdAndUserId(commentId, likeDto.getUserId());
        if (byCommentIdAndUserId.isEmpty()) {
            Like commentLike = likeMapper.toEntity(likeDto);
            Optional<Comment> commentById = commentRepository.findById(commentId);
            commentById.ifPresent(comment -> {
                comment.getLikes().add(commentLike);
                commentLike.setComment(comment);
            });
            likeRepository.save(commentLike);
            return likeMapper.toDto(commentLike);
        }
        return likeMapper.toDto(byCommentIdAndUserId.get());
    }

    public void deleteLikeOnPost(LikeDto likeDto) {
        isUserExist(likeDto);
        if (!postRepository.existsById(likeDto.getPostId())) {
            throw new EntityNotFoundException(String.format("post not found by id %d", likeDto.getPostId()));
        }
        likeRepository.deleteByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId());
    }

    public void deleteLikeOnComment(LikeDto likeDto) {
        isUserExist(likeDto);
        if (!commentRepository.existsById(likeDto.getCommentId())) {
            throw new EntityNotFoundException(String.format("post not found by id %d", likeDto.getCommentId()));
        }
        likeRepository.deleteByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId());
    }

    public List<LikeDto> getAllPostLikes(LikeDto likeDto) {
        isUserExist(likeDto);
        return likeRepository.findByPostId(likeDto.getPostId()).stream()
                .map(likeMapper::toDto)
                .collect(Collectors.toList());
    }

    private void isUserExist(LikeDto likeDto) {
        try {
            userServiceClient.getUser(likeDto.getUserId());
        } catch (FeignException.FeignClientException e) {
            throw new DataValidationException("This user doesn't exist");
        }
    }

    public List<UserDto> getUsersLikeFromPost(long postId){
        List<Like> listLike = likeRepository.findByPostId(postId);
        List<Long> userIds = listLike
                .stream()
                .map(like -> like.getUserId())
                .toList();
        return retrieveUsersByIds(userIds);
    }

    public List<UserDto> getUsersLikeFromComment(long commentId) {
        List<Like> listLike = likeRepository.findByCommentId(commentId);
        List<Long> userIds = listLike
                .stream()
                .map(like -> like.getUserId())
                .toList();
        return retrieveUsersByIds(userIds);
    }

    private List<UserDto> retrieveUsersByIds(List<Long> userIds) {
        List<UserDto> result = new ArrayList<>(userIds.size());
        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            int size = i + BATCH_SIZE;
            if (size > userIds.size()) {
                size = userIds.size();
            }
            List<Long> subId = userIds.subList(i, size);
            result.addAll(userServiceClient.getUsersByIds(subId));
        }
        return result;
    }
}
