package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.redis.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static faang.school.postservice.exception.MessagesForCommentsException.ID_IS_NULL;
import static faang.school.postservice.exception.MessagesForCommentsException.NO_COMMENTS_IN_THE_POST;
import static faang.school.postservice.exception.MessagesForCommentsException.NO_COMMENT_IN_DB;
import static faang.school.postservice.exception.MessagesForCommentsException.NO_USER_IN_DB;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    private final CommentRepository commentRepository;

    private final UserServiceClient userServiceClient;

    private final PostService postService;

    private final PostMapper postMapper;

    private final CommentEventPublisher commentEventPublisher;

    public CommentDto createComment(long id, CommentDto commentDto) {
        checkCommentDto(commentDto);
        Comment comment = commentMapper.ToEntity(commentDto);
        comment.setPost(postMapper.toEntity(postService.getPostById(id)));

        Comment savedComment = commentRepository.save(comment);

        sendCommentEvent(comment);

        return commentMapper.ToDto(savedComment);
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        checkCommentDto(commentDto);
        Comment comment = returnCommentIfExists(commentDto);

        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);

        return commentMapper.ToDto(comment);
    }

    public List<CommentDto> getAllComments(long id) {
        List<Comment> comments = commentRepository.findAllByPostId(postService.getPostById(id).getId());
        if (comments == null) {
            throw new DataValidationException(NO_COMMENTS_IN_THE_POST.getMessage());
        }
        comments.sort(Comparator.comparing(Comment::getCreatedAt));
        return commentMapper.ToDtoList(comments);
    }

    public void deleteComment(CommentDto commentDto) {
        checkCommentDto(commentDto);
        Comment comment = returnCommentIfExists(commentDto);
        commentRepository.delete(comment);
    }

    private void checkCommentDto(CommentDto commentDto) {
        if (userServiceClient.getUser(commentDto.getAuthorId()) == null) {
            throw new DataValidationException(NO_USER_IN_DB.getMessage());
        }
    }

    private Comment returnCommentIfExists(CommentDto commentDto) {
        if (commentDto.getId() == null) {
            throw new DataValidationException(ID_IS_NULL.getMessage());
        }
        return commentRepository.findById(commentDto.getId()).
                orElseThrow(() -> new DataValidationException(NO_COMMENT_IN_DB.getMessage()));
    }

    private void sendCommentEvent(Comment comment) {
        long commentAuthorId = comment.getAuthorId();
        long postAuthorId = comment.getPost().getAuthorId();

        if (commentAuthorId == postAuthorId) {
            return;
        }
        CommentEventDto commentEventDto = CommentEventDto.builder()
                .commentAuthorId(commentAuthorId)
                .postAuthorId(postAuthorId)
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .commentText(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();

        commentEventPublisher.publish(commentEventDto);
    }
}
