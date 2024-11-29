package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.comment.CommentException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.UserService;
import faang.school.postservice.service.cache.MultiGetCacheService;
import faang.school.postservice.service.cache.MultiSaveCacheService;
import faang.school.postservice.service.cache.SingleCacheService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final CommentEventPublisher commentEventPublisher;
    private final EventPublisher<CommentDto> commentFeedEventPublisher;
    private final SingleCacheService<Long, UserDto> userCacheService;
    private final MultiGetCacheService<Long, CommentDto> commentGetCacheService;
    private final MultiSaveCacheService<CommentDto> commentSaveCacheService;

    @Override
    public CommentDto addComment(CommentDto commentDto) {
        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());

        Post post = postRepository
                .findById(commentDto.getPostId())
                .orElseThrow(()
                        -> new EntityNotFoundException(String.format("Post with ID %s not found.", commentDto.getPostId()))
                );

        Comment comment = commentMapper.toComment(commentDto);
        comment.setPost(post);

        comment = commentRepository.save(comment);
        userCacheService.save(userDto.getId(), userDto);
        CommentDto commentDtoWithId = commentMapper.toDto(comment);
        publishEvents(commentDtoWithId);

        return commentDtoWithId;
    }

    @Override
    @Transactional
    public void updateComment(long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with ID %s not found.", commentId)));

        long dtoAuthorId = updateCommentDto.authorId();
        if (dtoAuthorId != comment.getAuthorId()) {
            throw new CommentException(String.format("User with ID %s is not allowed to update this comment.",
                    dtoAuthorId));
        }

        commentRepository.updateContentAndDateById(commentId, updateCommentDto.content(), LocalDateTime.now());
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.getByPostIdOrderByCreatedAtDesc(postId);
        return commentMapper.toDto(comments);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId, long count) {
        List<CommentDto> comments = commentGetCacheService.getAll(postId);

        if (comments.size() < count) {
            long requiredCount = count - comments.size();
            List<Comment> missingPosts = commentRepository.getByPostIdWithLimit(postId, requiredCount);
            List<CommentDto> missingPostDtos = commentMapper.toDto(missingPosts);

            comments.addAll(missingPostDtos);
            commentSaveCacheService.saveAll(missingPostDtos);
        }
        return comments;
    }

    @Override
    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public void assignAuthorsToComments(List<CommentDto> commentDtos) {
        List<Long> commentAuthorIds = commentDtos.stream()
                .map(CommentDto::getAuthorId)
                .toList();
        List<UserDto> commentAuthors = userService.getUsersFromCacheOrService(commentAuthorIds);

        Map<Long, UserDto> authorMap = commentAuthors.stream()
                .collect(Collectors.toMap(
                        UserDto::getId,
                        Function.identity(),
                        (existing, replacement) -> existing)
                );

        commentDtos.forEach(comment -> {
            UserDto author = authorMap.getOrDefault(comment.getAuthorId(), null);
            comment.setAuthor(author);
        });
    }

    private void publishEvents(CommentDto commentDto) {
        CommentEvent commentEvent = new CommentEvent(commentDto.getId(), commentDto.getAuthorId(),
                commentDto.getPostId(), LocalDateTime.now());
        commentEventPublisher.publish(commentEvent);

        commentFeedEventPublisher.publish(commentDto);

        log.info("comment event published to topic, event: {}", commentEvent);
    }
}
