package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.ChangeCommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;

    @Transactional
    public CreateCommentDto createComment(CreateCommentDto createCommentDto) {
        Comment comment = commentMapper.toEntity(createCommentDto);
        Comment commentSaved = commentRepository.save(comment);
        log.debug("Comment saved in db. Comment: {}", commentSaved);

        return commentMapper.toDto(commentSaved);
    }

    @Transactional
    public CreateCommentDto changeComment(ChangeCommentDto changeCommentDto) {
        Comment commentFromDB = commentRepository.findById(changeCommentDto.getId())
                .orElseThrow(() -> {
                    log.error("couldn't find a comment by id: {}", changeCommentDto.getId());
                    return new DataValidationException("couldn't find a comment by id: " + changeCommentDto.getId());
                });

        log.debug("Received comment for modification: {}", commentFromDB);

        commentFromDB.setContent(changeCommentDto.getContent());

        return commentMapper.toDto(commentFromDB);
    }

    @Transactional(readOnly = true)
    public List<CreateCommentDto> getAllCommentsOnPostId(long id) {
        commentValidator.getAllCommentsOnPostIdService(id);

        List<Comment> comments = commentRepository.findAllByPostIdOrderByCreatedAtDesc(id);
        log.debug("Received list of comments for post with id: {} ; CommentList: {}", id, comments);
        return comments.stream().map(commentMapper::toDto).toList();
    }

    @Transactional
    public void deleteComment(long id) {
        commentRepository.deleteById(id);
    }
}
