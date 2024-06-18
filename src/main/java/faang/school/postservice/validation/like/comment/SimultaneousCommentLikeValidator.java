package faang.school.postservice.validation.like.comment;

import org.springframework.stereotype.Component;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validation.like.SimultaneousLikeValidator;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class SimultaneousCommentLikeValidator implements SimultaneousLikeValidator {
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    
    @Override
    public void verifyNotExists(LikeDto dto) {
        verifyUserDidntAlreadyLikedComment(dto);
        
        verifyLikeNotOnPostAndCommentSimultaneously(dto);
    }
    
    private void verifyLikeNotOnPostAndCommentSimultaneously(LikeDto dto) {
        if (likeOnPostAndCommentExist(dto)) {
            throwAlreadyExistsException(String.format(
                "User with id %d can't like post and comment simultaneously",
                dto.getUserId())
            );
        }
    }
    
    private boolean likeOnPostAndCommentExist(LikeDto dto) {
        return getCommentById(dto.getCommentId()).getPost().getLikes()
            .stream()
            .map(like -> like.getUserId())
            .anyMatch(userIdFromLike -> userIdFromLike.equals(dto.getUserId()));
    }
    
    private void verifyUserDidntAlreadyLikedComment(LikeDto dto) {
        Long commentId = dto.getCommentId();
        Long userId = dto.getUserId();
        likeRepository.findByCommentIdAndUserId(commentId, userId).ifPresent(
            (like) -> throwAlreadyExistsException(
                String.format(
                    "User with id %d already has like on post with Id %d",
                    userId,
                    commentId
                )
            )
        );
    }
    
    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
            () -> new NotFoundException(String.format("Comment with id %d not found", commentId))
        );
    }
}
