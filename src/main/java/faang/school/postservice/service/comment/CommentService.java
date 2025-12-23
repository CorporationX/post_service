package faang.school.postservice.service.comment;

import faang.school.postservice.aop.PublishCommentEvent;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.author.AuthorDto;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.commentanalysis.AnalysisCommentsProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.author.AuthorCacheService;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final AuthorCacheService authorCacheService;

    @PublishCommentEvent
    @Transactional
    public Comment create(CommentCreateDto commentCreateDto, Long userId) {
        CommentValidator.validateCommentContent(commentCreateDto.content());

        UserDto user = userServiceClient.getUser(userId);
        CommentValidator.validateUser(user);

        Post post = postRepository.getByIdOrThrow(commentCreateDto.postId());

        Comment comment = CommentMapper.toEntity(commentCreateDto, post, userId);
        comment = commentRepository.save(comment);

        AuthorDto authorDto = new AuthorDto(
                String.valueOf(user.id()),
                user.username(),
                null
        );

        authorCacheService.put(authorDto);

        log.info("Creating comment for postId={} by userId={}", post.getId(), userId);
        return comment;
    }

    @Transactional
    public Comment update(Long commentId, CommentUpdateDto dto, Long userId) {
        Comment existing = commentRepository.findByIdOrThrow(commentId);

        if (!existing.getAuthorId().equals(userId)) {
            throw new ValidationException("You can't edit someone else's comment.");
        }
        CommentValidator.validateCommentContent(dto.content());

        existing.setContent(dto.content());
        existing.setIsVerified(null);
        log.info("Updating comment id={} by userId={}", commentId, userId);

        return commentRepository.save(existing);
    }

    public PageResponse<CommentDto> findAllByPostId(Long postId, Pageable pageable) {
        postRepository.getByIdOrThrow(postId);

        Page<Comment> page = commentRepository.findAllByPostId(postId, pageable);

        return PageResponse.from(page, CommentMapper::toDto);
    }

    public void delete(Long commentId, Long userId) {
        Comment existing = commentRepository.findByIdOrThrow(commentId);

        CommentValidator.validateCommentOwnership(existing.getAuthorId(), userId);

        commentRepository.delete(existing);
        log.info("Deleting comment id={} by userId={}", commentId, userId);
    }

    public Comment getById(Long commentId) {
        log.info("Fetching comment by id={}", commentId);
        return commentRepository.findByIdOrThrow(commentId);
    }
}