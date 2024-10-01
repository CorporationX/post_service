package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.publishable.CommentEvent;
import faang.school.postservice.dto.user.CachedUserDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final ModerationDictionary moderationDictionary;
    private final ExecutorService moderationExecutor;
    private final KafkaCommentProducer commentProducer;
    private final RedisUserRepository redisUserRepository;
    private final UserServiceClient userServiceClient;

    @Value("${comment.batchSize}")
    private int batchSize;

    public CommentService(
            CommentRepository commentRepository,
            CommentMapper commentMapper,
            CommentValidator commentValidator,
            ModerationDictionary moderationDictionary,
            @Qualifier("moderation-thread-pool") ExecutorService moderationExecutor,
            KafkaCommentProducer commentProducer,
            RedisUserRepository redisUserRepository,
            UserServiceClient userServiceClient
    ) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.commentValidator = commentValidator;
        this.moderationDictionary = moderationDictionary;
        this.moderationExecutor = moderationExecutor;
        this.commentProducer = commentProducer;
        this.redisUserRepository = redisUserRepository;
        this.userServiceClient = userServiceClient;
    }

    @Transactional
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        commentValidator.findPostById(postId);
        Comment savedComment = commentRepository.save(commentMapper.toEntity(commentDto));

        CommentEvent commentEvent = CommentEvent.builder()
                .authorId(commentDto.getAuthorId())
                .postId(commentDto.getPostId())
                .build();

        UserDto userDto = userServiceClient.getUser(savedComment.getAuthorId());
        CachedUserDto cachedUserDto = CachedUserDto.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .subscriberIds(userDto.getSubscriberIds())
                .build();

        redisUserRepository.save(cachedUserDto.getId(), cachedUserDto);
        commentProducer.sendEvent(commentEvent);
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

    public CommentDto findById(long id) {
        return commentMapper.toDto(commentRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new));

    }
}
