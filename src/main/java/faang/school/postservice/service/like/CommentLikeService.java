package faang.school.postservice.service.like;

import org.springframework.stereotype.Service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validation.like.comment.CommentLikeValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeValidator validator;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final LikeMapper mapper;

    public LikeDto likeComment(LikeDto dto) {
        validator.verifyCanLikeComment(dto);
        
        Like like = mapper.toModel(dto);
        like.setComment(getCommentById(dto.getCommentId()));
        likeRepository.save(like);
        
        return mapper.toDto(like);
    }
    
    public void deleteCommentLike(LikeDto dto) {
        Long userId = dto.getUserId();
        validator.verifyLikeExists(userId);
        
        likeRepository.deleteByCommentIdAndUserId(dto.getCommentId(), userId);
    }
    
    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
            () -> new NotFoundException(String.format("Comment with id %d not found", commentId))
        );
    }
}
