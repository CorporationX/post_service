package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.ModerationDictionary;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final ModerationDictionary moderationDictionary;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final CommentEventPublisher commentEventPublisher;
    @Value("${post.moderateComment.batchSize}")
    private Integer batchSize;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        validateExistingUser(commentDto);
        validateExistingPost(commentDto);
        Comment comment = commentMapper.toEntity(commentDto);
        CommentDto savedEventDto = commentMapper.toDto(commentRepository.save(comment));
        commentEventPublisher.publish(CommentEventDto.builder()
                .postId(commentDto.getPostId())
                .authorId(commentDto.getAuthorId())
                .commentId(commentDto.getId())
                .createdAt(LocalDateTime.now().withNano(0))
                .build());
        return savedEventDto;
    }

    @Transactional
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream()
                .sorted((comment1, comment2) -> comment1.getCreatedAt().compareTo(comment2.getCreatedAt()))
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public boolean deleteComment(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id: " + commentId + " not found"));
        commentRepository.deleteById(comment.getId());
        return true;
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new NotFoundException("Comment with id: " + commentDto.getId() + " not found"));
        validateToUpdateComment(commentDto, comment);
        comment.setContent(commentDto.getContent());
        return commentMapper.toDto(commentRepository.save(comment));

    }

    @Transactional
    public void moderateComment() {
        List<Comment> comments = commentRepository.findNotVerified();
        List<List<Comment>> grouped = new ArrayList<>();

        for (int i = 0; i < comments.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, comments.size());
            grouped.add(comments.subList(i, endIndex));
        }

        List<CompletableFuture<Void>> completableFutures = grouped.stream()
                .map(list -> CompletableFuture.runAsync(() -> verifyComment(list)))
                .toList();
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
    }

    @Transactional
    public void verifyComment(List<Comment> comments) {
        Predicate<Comment> noUnwantedWords = comment -> !moderationDictionary.containsBadWord(comment.getContent());
        Consumer<Comment> verifyComment = comment -> {
            comment.setVerified(true);
            comment.setVerifiedDate(LocalDateTime.now());
        };

        List<Comment> verifiedComments = comments.stream()
                .filter(noUnwantedWords)
                .peek(verifyComment)
                .toList();
        commentRepository.saveAll(verifiedComments);
    }

    @Transactional(readOnly = true)
    public boolean existById(long commentId) {
        return commentRepository.existsById(commentId);
    }

    @Transactional(readOnly = true)
    public Long getAuthorId(long postId) {
        return commentRepository
                .findById(postId)
                .orElseThrow(() -> new NotFoundException("Comment not found. Id: " + postId))
                .getAuthorId();
    }

    @Retryable(retryFor = {FeignException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    private void validateExistingUser(CommentDto commentDto) {
        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        if (userDto == null || userDto.getId() == null) {
            throw new NotFoundException("Author with id: " + commentDto.getAuthorId() + " not found!");
        }
    }

    @Retryable(retryFor = {FeignException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    private void validateExistingPost(CommentDto commentDto) {
        Post post = postService.getPostById(commentDto.getPostId());
        if (post == null || post.getId() < 1) {
            throw new NotFoundException("Post with id: " + commentDto.getAuthorId() + " not found!");
        }
    }

    private void validateToUpdateComment(CommentDto commentDto, Comment comment) {
        if (comment.getAuthorId() != commentDto.getAuthorId() ||
                comment.getPost().getId() != commentDto.getPostId()) {
            throw new DataValidException("Comment with id: " + commentDto.getId() + " not valid");
        }
    }
}