package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.kafka.producer.KafkaEventProducer;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.error.CommentServiceErrors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository repository;
    private final PostRepository postRepository;
    private final CommentMapper mapper;
    private final KafkaEventProducer kafkaEventProducer;

    @Value("${spring.data.kafka.topics.comment_topic}")
    private String commentTopic;

    public CommentDto addComment(Long postId, CommentDto commentDto) {
        validateComment(postId, commentDto);

        Comment comment = mapper.toEntity(commentDto);
        Post post = getValidPost(postId);
        comment.setPost(post);
        repository.save(comment);
        post.getComments().add(comment);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        kafkaEventProducer.sendCommentEvent(mapper.toCommentEvent(comment));
        return mapper.toDto(comment);
    }

    public void validateComment(Long postId, CommentDto commentDto) {
        if (commentDto.getContent() == null || commentDto.getContent().isBlank()) {
            throw new IllegalArgumentException(CommentServiceErrors.COMMENT_IS_EMPTY.getValue());
        }
        if (commentDto.getContent().length() > 4096) {
            throw new IllegalArgumentException(CommentServiceErrors.COMMENT_TOO_LONG.getValue());
        }
    }


    public CommentDto updateComment(Long postId, CommentDto commentDto) {
        getValidPost(postId);
        Comment comment = repository.findById(commentDto.getId()).orElse(null);
        if (comment == null) {
            throw new IllegalArgumentException(CommentServiceErrors.COMMENT_NOT_FOUND.getValue());
        }
        CommentDto currentCommentDto = mapper.toDto(comment);
        equalUpdateComment(commentDto, currentCommentDto);
        commentDto.setUpdatedAt(LocalDateTime.now());
        Comment newComment = mapper.toEntity(commentDto);
        return mapper.toDto(repository.save(newComment));

    }

    public List<CommentDto> getComments(Long postId) {
        getValidPost(postId);
        List<Comment> comments = repository.findAllByPostId(postId);
        List<CommentDto> commentDtos = mapper.toDto(comments);
        return commentDtos.stream()
                .sorted(this::localDateComparator)
                .toList();
    }

    public CommentDto getComment(Long commentId) {
        Comment comment = getCommentByIdOrFail(commentId);
        return mapper.toDto(comment);
    }

    public CommentDto deleteComment(Long postId, CommentDto commentDto) {
        getValidPost(postId);
        repository.deleteById(commentDto.getId());
        return commentDto;
    }

    private Comment getCommentByIdOrFail(Long commentId) {
        return repository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }

    private Post getValidPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new IllegalArgumentException(CommentServiceErrors.POST_NOT_FOUND.getValue());
        }
        return post;
    }

    private void equalUpdateComment(CommentDto commentDto, CommentDto currentCommentDto) {
        if (!Objects.equals(commentDto.getAuthorId(), currentCommentDto.getAuthorId())
                || !Objects.equals(commentDto.getLikes(), currentCommentDto.getLikes())
                || !Objects.equals(commentDto.getPostId(), currentCommentDto.getPostId())
                || !Objects.equals(commentDto.getCreatedAt(), currentCommentDto.getCreatedAt())
                || !Objects.equals(commentDto.getUpdatedAt(), currentCommentDto.getUpdatedAt())
        ) {
            throw new IllegalArgumentException(CommentServiceErrors.CHANGE_NOT_COMMENT.getValue());
        }
    }

    private int localDateComparator(CommentDto commentLeft, CommentDto commentRight) {
        if (commentLeft.getCreatedAt().isAfter(commentRight.getCreatedAt())) {
            return 1;
        } else if (commentLeft.getCreatedAt().isBefore(commentRight.getCreatedAt())) {
            return -1;
        } else {
            return 0;
        }
    }
}
