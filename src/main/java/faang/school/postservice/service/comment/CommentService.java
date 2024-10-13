package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedCommentEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.feed.FeedService;
import faang.school.postservice.validator.comment.CommentValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final ModerationDictionary moderationDictionary;
    private final ExecutorService moderationExecutor;
    private final FeedService feedService;

    @Value("${comment.batchSize}")
    private int batchSize;

    public CommentService(
            CommentRepository commentRepository,
            CommentMapper commentMapper,
            CommentValidator commentValidator,
            ModerationDictionary moderationDictionary,
            @Qualifier("moderation-thread-pool") ExecutorService moderationExecutor,
            FeedService feedService
    ) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.commentValidator = commentValidator;
        this.moderationDictionary = moderationDictionary;
        this.moderationExecutor = moderationExecutor;
        this.feedService = feedService;
    }

    @Transactional
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        commentValidator.findPostById(postId);
        Comment savedComment = commentRepository.save(commentMapper.toEntity(commentDto));

        feedService.createAndSendFeedCommentEvent(new FeedCommentEvent(
                commentDto.getId(), commentDto.getPostId(), commentDto.getAuthorId(), commentDto.getContent()));

        return commentMapper.toDto(savedComment);
    }

    @Transactional
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto) {
        commentValidator.findPostById(postId);
        Comment comment = commentValidator.findCommentById(commentId);
        commentValidator.checkUserRightsToChangeComment(comment, commentDto);
        comment.setContent(commentDto.getContent());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPost(Long postId) {
        commentValidator.findPostById(postId);
        List<Comment> comments = commentRepository.findAllByPostIdSorted(postId);
        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentValidator.findCommentById(commentId);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void verifyComments() {
        List<Comment> commentsToVerify = commentRepository.findAllByVerifiedDateIsNull();

        List<List<Comment>> partitions = partitionList(commentsToVerify, batchSize);

        List<CompletableFuture<Void>> futures = partitions.stream()
                .map(this::moderatePartition)
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private CompletableFuture<Void> moderatePartition(List<Comment> partition) {
        return CompletableFuture.runAsync(() -> {
            for (Comment comment : partition) {
                boolean containsBadWords = moderationDictionary.containsForbiddenWords(comment.getContent());
                comment.setVerified(!containsBadWords);
                comment.setVerifiedDate(LocalDateTime.now());
            }
            commentRepository.saveAll(partition);
        }, moderationExecutor);
    }

    private List<List<Comment>> partitionList(List<Comment> list, int partitionSize) {
        List<List<Comment>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += partitionSize) {
            partitions.add(new ArrayList<>(list.subList(i, Math.min(list.size(), i + partitionSize))));
        }
        return partitions;
    }
}
