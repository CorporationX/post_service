package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeValidator likeValidator;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;

    public LikeDto createLikeForPost (LikeDto likeDto) {
        likeValidator.validatePostExists(likeDto);
        likeValidator.validateUserExists(likeDto);
        likeValidator.validateLikeOnPostNotExist(likeDto);

        Like postLike = likeMapper.toEntity(likeDto);
        postRepository.findById(likeDto.getPostId()).ifPresent(post -> post.getLikes().add(postLike));
        likeRepository.save(postLike);
        return likeMapper.toDto(postLike);
    }

    public void deleteLikeForPost (LikeDto likeDto) {
        likeRepository.deleteByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId());

    }

    public LikeDto createLikeForComment (LikeDto likeDto) {
        likeValidator.validateCommentExists(likeDto);
        likeValidator.validateUserExists(likeDto);
        likeValidator.validateLikeOnCommentNotExist(likeDto);

        Like commentLike = likeMapper.toEntity(likeDto);
        commentRepository.findById(likeDto.getCommentId()).ifPresent(comment -> comment.getLikes().add(commentLike));
        likeRepository.save(commentLike);
        return likeMapper.toDto(commentLike);
    }

    public void deleteLikeForComment (LikeDto likeDto) {
        likeRepository.deleteByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId());

    }
}
