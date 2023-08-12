package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;

    public LikeDto createLikeOnPost(LikeDto likeDto) {
        isUserExist(likeDto);
        long postId = likeDto.getPostId();
        Optional<Like> byPostIdAndUserId = likeRepository.findByPostIdAndUserId(postId, likeDto.getUserId());
        if (byPostIdAndUserId.isEmpty()) {
            Like postLike = likeMapper.toEntity(likeDto);
            Optional<Post> postById = postRepository.findById(postId);
            postById.get().getLikes().add(postLike);
            postLike.setPost(postById.get());
            likeRepository.save(postLike);
            return likeMapper.toDto(postLike);
        }
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
}
