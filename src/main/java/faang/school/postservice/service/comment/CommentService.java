package faang.school.postservice.service.comment;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundEntityException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Comment validationAndCommentsReceived(LikeDto likeDto) {
        if (likeDto.getCommentId() != null) {
            if (!commentRepository.existsById(likeDto.getCommentId())) {
                throw new DataValidationException("no such postId exists commentId: " + likeDto.getCommentId());
            }
        } else {
            throw new DataValidationException("arrived likeDto with postId and commentId equal to null");
        }
        return commentRepository.findById(likeDto.getCommentId()).orElseThrow(() ->
                new NotFoundEntityException("Not found comment by id: " + likeDto.getCommentId()));
    }
}
