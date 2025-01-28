package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private static final int BATCH_SIZE = 100;

    private final UserServiceClient userServiceClient;
    private final LikeValidator likeValidator;
    private final LikeRepository likeRepository;
    private final UserContext userContext;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeMapper likeMapper;
    private final LikeEventPublisher likeEventPublisher;

    public LikeDto getLikeById(long id) {
        return likeMapper.toDto(likeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Like with id %d not found", id)))
        );
    }

    public List<LikeDto> getAllLikes() {
        return likeRepository.findAll().stream()
                .map(likeMapper::toDto)
                .toList();
    }

    public LikeDto addLike(LikeDto likeDto) {
        long userId = userContext.getUserId();

        likeValidator.validateAdded(likeDto, userId,
                postService.existsPostById(likeDto.getPostId()),
                commentService.existsCommentById(likeDto.getCommentId()),
                existsLikeByCommentIdAndUserId(likeDto.getCommentId(), userId),
                existsLikeByPostIdAndUserId(likeDto.getPostId(), userId)
        );

        Like addedLike = likeMapper.toEntity(likeDto);
        addedLike.setUserId(userId);
        setLikeObject(likeDto, addedLike);
        addedLike.setCreatedAt(LocalDateTime.now());

        likeEventPublisher.sendMessage(likeMapper.toEvent(addedLike));

        return likeMapper.toDto(likeRepository.save(addedLike));
    }

    public void deleteLike(LikeDto likeDto) {
        likeValidator.validateDeleted(existsLikeById(likeDto.getId()));
        likeRepository.deleteById(likeDto.getId());
    }

    public List<UserDto> getAllLikedByPostId(Long id) {
        return getAllLikedById(id, true);
    }

    public List<UserDto> getAllLikedByCommentId(Long id) {
        return getAllLikedById(id, false);
    }

    private List<UserDto> getAllLikedById(Long id, boolean isPost) {
        List<Like> likes = isPost ? likeRepository.findByPostId(id) : likeRepository.findByCommentId(id);
        List<Long> likeIds = likes.stream()
                .map(Like::getUserId)
                .toList();

        List<List<Long>> batches = splitIntoBatches(likeIds, BATCH_SIZE);
        List<UserDto> users = new ArrayList<>();

        for (List<Long> batch: batches) {
            List<UserDto> batchUsers = userServiceClient.getUsersByIds(batch);
            users.addAll(batchUsers);
        }
        return users;
    }

    private List<List<Long>> splitIntoBatches(List<Long> list, int batchSize) {
        List<List<Long>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            batches.add(new ArrayList<>(list.subList(i, end)));
        }
        return batches;
    }

    private boolean existsLikeById(long id) {
        return likeRepository.existsById(id);
    }

    private boolean existsLikeByCommentIdAndUserId(Long commentId, long userId) {
        if (commentId == null) return false;
        return likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent();
    }

    private boolean existsLikeByPostIdAndUserId(Long postId, long userId) {
        if (postId == null) return false;
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    private void setLikeObject(LikeDto likeDto, Like like) {
        if (likeDto.getPostId() != null) {
            like.setPost(postService.getPost(likeDto.getPostId()));
        }

        if (likeDto.getCommentId() != null) {
            like.setComment(commentService.getComment(likeDto.getCommentId()));
        }
    }

}
