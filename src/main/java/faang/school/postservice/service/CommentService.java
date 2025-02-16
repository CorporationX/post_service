package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.ModerationDictionary;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final CommentValidator commentValidator;
    private final ModerationDictionary moderationDictionary;


    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Long postId) {
        postService.get(postId);

        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .toList();
    }

    @Transactional
    public Comment createComment(Comment comment, Long postId, Long authorId) {
        Post post = postService.get(postId);
        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException e) {
            throw new UserNotFoundException("User with id = " + authorId + " was not found");
        }

        comment.setPost(post);
        comment.setAuthorId(authorId);

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Long commentId, Comment updatedComment, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("There is no comment with id; " + commentId));

        commentValidator.validateAuthor(comment, userId);
        commentValidator.validateCommentUpdate(updatedComment);

        String updatedContent = updatedComment.getContent();
        comment.setContent(updatedContent);

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("There is no comment with id + " + commentId));
        commentValidator.validateAuthor(comment, userId);

        commentRepository.delete(comment);

        return comment;
    }

    @Transactional
    public int moderateComments() {
        List<Comment> unverifiedComments = commentRepository.findUnverifiedComments();

        if (unverifiedComments.isEmpty()) {
            log.info("No unverified comments to moderate");
            return 0;
        }

        unverifiedComments.parallelStream()
                .peek(comment -> log.info("Moderating comment ID: {}", comment.getId()))
                .forEach(comment -> {
                    boolean containsBannedWords = moderationDictionary.containsBannedWords(comment.getContent());
                    comment.setVerified(!containsBannedWords);
                    comment.setVerifiedDate(LocalDateTime.now());
                });

        commentRepository.saveAll(unverifiedComments);
        log.info("Moderated {} comments", unverifiedComments.size());

        return unverifiedComments.size();
    }
}
