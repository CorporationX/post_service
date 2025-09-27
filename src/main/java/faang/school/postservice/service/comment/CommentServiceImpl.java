package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.avro.CommentCreatedEventAvro;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с комментариями к постам.
 * <p>
 * Предоставляет функциональность для создания, обновления, удаления
 * и получения комментариев. Обеспечивает транзакционность операций
 * и проверку прав доступа пользователей.
 * </p>
 *
 * @author bozya
 * @since 21.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper mapper;
    private final UserContext context;
    private final KafkaCommentProducer producer;

    @Override
    @Transactional
    public CommentViewDto create(CommentCreateDto createDto, Long postId) {
        Post post = findPostById(postId);

        Comment comment = Comment.builder()
            .content(createDto.content())
            .authorId(context.getUserId())
            .post(post)
            .largeImageFileKey(createDto.largeImageFileKey())
            .smallImageFileKey(createDto.smallImageFileKey())
            .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Комментарий успешно создан");


        CommentCreatedEventAvro event = mapper.toAvro(savedComment);
        producer.sendCommentWithRetry(event);

        return mapper.toViewDto(savedComment);
    }

    @Override
    @Transactional
    public void delete(Long postId, Long commentId) {
        Comment comment = findCommentById(postId, commentId);
        commentRepository.delete(comment);
        log.info("Комментарий удален");
    }

    @Override
    @Transactional
    public CommentViewDto update(Long postId, Long commentId, CommentUpdateDto updateDto) {
        Long authorId = context.getUserId();

        Comment updatedComment = commentRepository.update(
            commentId,
            postId,
            authorId,
            updateDto.content(),
            updateDto.largeImageFileKey(),
            updateDto.smallImageFileKey())
            .orElseThrow(() -> new EntityNotFoundException(
            "Комментарий не найден или у вас нет прав для его редактирования"
        ));

        log.info("Комментарий с ID {} обновлен пользователем {}", commentId, authorId);
        return mapper.toViewDto(updatedComment);
    }

    @Override
    @Transactional
    public List<CommentViewDto> getAllCommentByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Пост с id " + postId + " не найден");
        }

        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .map(mapper::toViewDto)
                .collect(Collectors.toList());
    }

    private Post findPostById(Long postId) {
        return postRepository.findPostOrThrow(postId);
    }

    private Comment findCommentById(Long postId, Long commentId) {
        return commentRepository.findByIdAndPostIdOrThrow(postId, commentId);
    }
}