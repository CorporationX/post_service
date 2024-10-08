package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like.LikeValidator;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private static final int BATCH_SIZE = 100;

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;
    private final LikeValidator likeValidator;

    public List<UserDto> getAllUsersByPostId(long id) {
        return getUsersBatched(getUsersIdsByLikes(getUsersIdsByPostId(id)));
    }

    public List<UserDto> getAllUsersByCommentId(long id) {
        return getUsersBatched(getUsersIdsByLikes(getUserIdsByCommentId(id)));
    }

    private List<Like> getUserIdsByCommentId(long id) {
        return likeRepository.findByCommentId(id);
    }

    private List<Like> getUsersIdsByPostId(long id) {
        return likeRepository.findByPostId(id);
    }

    private List<Long> getUsersIdsByLikes(List<Like> likes) {
        return likes.stream()
                .map(Like::getUserId)
                .toList();
    }

    private List<UserDto> getUsersBatched(List<Long> userIds) {
        long batchSize = userIds.size() / BATCH_SIZE + 1;
        List<UserDto> usersLiked = new ArrayList<>();
        for (int i = 0; i < batchSize; i += BATCH_SIZE) {
            int batchEnd = Math.min(i + BATCH_SIZE, userIds.size());
            usersLiked.addAll(userServiceClient.getUsersByIds(userIds.subList(i, batchEnd)));
        }
        return usersLiked;
    }

    public LikeResponseDto addLike(LikeRequestDto likeRequestDto) {
        validateUserExists(likeRequestDto.getUserId());
        if (likeRequestDto.getPostId() == null && likeRequestDto.getCommentId() == null) {
            throw new IllegalArgumentException("Like must target either a post or a comment");
        }

        Like like = likeMapper.toEntity(likeRequestDto);

        if (likeRequestDto.getPostId() != null) {
            Post post = postRepository.findById(likeRequestDto.getPostId())
                    .orElseThrow(() -> new IllegalArgumentException("Post with ID " + likeRequestDto.getPostId() + " not found"));
            
            likeValidator.validateLikeForPostExists(likeRequestDto.getPostId(), likeRequestDto.getUserId());

            like.setPost(post);
        } else {
            Comment comment = commentRepository.findById(likeRequestDto.getCommentId())
            .orElseThrow(() -> new IllegalArgumentException("Comment with ID " + likeRequestDto.getCommentId() + " not found"));
            
            likeValidator.validateLikeForCommentExists(likeRequestDto.getCommentId(), likeRequestDto.getUserId());
            
            like.setComment(comment);
        }
        
        likeRepository.save(like);
        return likeMapper.toResponseDto(like);
    }

    public void removeLike(Long likeId) {
        if (!likeRepository.existsById(likeId)) {
            throw new IllegalArgumentException("Like with ID " + likeId + " not found");
        }
        likeRepository.deleteById(likeId);
    }

    private void validateUserExists(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (Exception e) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
    }
}
