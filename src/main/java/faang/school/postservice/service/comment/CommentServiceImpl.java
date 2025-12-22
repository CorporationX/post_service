package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.PublishCommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserContext userContext;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final SpringCommentPublisher commentPublisher;


    @Transactional
    @Override
    public ResponseCommentDto createComment(CreateCommentDto createCommentDto) {
        Post post = postRepository.findById(createCommentDto.postId())
                .orElseThrow(() -> new EntityNotFoundException("No entity found for the specified %d id!"
                        .formatted(createCommentDto.postId())));
        Comment newComment = commentMapper.toEntity(createCommentDto);
        newComment.setPost(post);
        newComment.setAuthorId(userContext.getUserId());
        ResponseCommentDto dto = commentMapper.toDto(commentRepository.save(newComment));

        commentPublisher.handleCommentCreated(new ModelEventDto(userContext.getUserId(), newComment.getId()));

        return dto;
    }

    @Transactional
    @Override
    public ResponseCommentDto updateComment(long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = getCommentByIdOrThrow(commentId);
        validateAuthorComment(comment);
        comment.setContent(updateCommentDto.content());
        return commentMapper.toDto(comment);
    }

    @Override
    public List<ResponseCommentDto> getComments(long postId, int page, int pageSize) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("This post for %d - id does not exist!".formatted(postId));
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = postRepository.findAllCommentByPostId(postId, pageable);
        return commentPage.map(commentMapper::toDto).getContent();
    }

    @Transactional
    @Override
    public void deleteComment(long commentId) {
        Comment comment = getCommentByIdOrThrow(commentId);
        validateAuthorComment(comment);
        commentRepository.delete(comment);
    }


    private Comment getCommentByIdOrThrow(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("No entity found for the specified %d id!"
                        .formatted(commentId)));
    }

    private void validateAuthorComment(Comment comment) {
        if (!Objects.equals(comment.getAuthorId(), userContext.getUserId())) {
            throw new ForbiddenException("You cannot edit someone else's comment!");
        }
    }
}