package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        long postId = likeDto.getPost().getId();
        Optional<Like> byPostIdAndUserId = likeRepository.findByPostIdAndUserId(postId, likeDto.getUserId());
        if (byPostIdAndUserId.isEmpty()) {
            Like postLike = likeMapper.toEntity(likeDto);
            postRepository.findById(postId).ifPresent(post -> post.getLikes().add(postLike));
            return likeMapper.toDto(postLike);
        }
        return likeMapper.toDto(byPostIdAndUserId.get());
    }

    public LikeDto createLikeOnComment(LikeDto likeDto) {
        isUserExist(likeDto);
        long commentId = likeDto.getComment().getId();
        Optional<Like> byCommentIdAndUserId = likeRepository.findByCommentIdAndUserId(commentId, likeDto.getUserId());
        if (byCommentIdAndUserId.isEmpty()) {
            Like commentLike = likeMapper.toEntity(likeDto);
            commentRepository.findById(commentId).ifPresent(comment -> comment.getLikes().add(commentLike));
            return likeMapper.toDto(commentLike);
        }
        return likeMapper.toDto(byCommentIdAndUserId.get());
    }

    private void isUserExist(LikeDto likeDto) {
        try {
            userServiceClient.getUser(likeDto.getUserId());
        }
        catch (DataValidationException e) {
            throw new DataValidationException("This user doesn't exist");
        }
    }
}
