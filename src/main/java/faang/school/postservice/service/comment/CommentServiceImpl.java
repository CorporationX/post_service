package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
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

    /**
     * Создает новый комментарий к указанному посту.
     * <p>
     * Проверяет существование поста, устанавливает автора комментария
     * из контекста текущего пользователя и сохраняет комментарий в базе данных.
     * </p>
     *
     * @param createDto DTO с данными для создания комментария
     * @param postId идентификатор поста, к которому добавляется комментарий
     * @return DTO созданного комментария
     * @throws EntityNotFoundException если пост с указанным идентификатором не найден
     */
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

        return mapper.toViewDto(savedComment);
    }

    /**
     * Удаляет комментарий по идентификаторам поста и комментария.
     * <p>
     * Проверяет существование комментария перед удалением.
     * </p>
     *
     * @param postId идентификатор поста
     * @param commentId идентификатор комментария
     * @throws EntityNotFoundException если комментарий с указанным идентификатором не найден
     */
    @Override
    @Transactional
    public void delete(Long postId, Long commentId) {
        Comment comment = findCommentById(postId, commentId);
        commentRepository.delete(comment);
        log.info("Комментарий удален");
    }

    /**
     * Обновляет существующий комментарий.
     * <p>
     * Проверяет права доступа пользователя (только автор может редактировать комментарий)
     * и обновляет содержимое комментария.
     * </p>
     *
     * @param postId идентификатор поста
     * @param commentId идентификатор комментария
     * @param updateDto DTO с обновленными данными комментария
     * @return DTO обновленного комментария
     * @throws EntityNotFoundException если комментарий не найден или пользователь не является автором
     */
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
                updateDto.smallImageFileKey()
        ).orElseThrow(() -> new EntityNotFoundException(
                "Комментарий не найден или у вас нет прав для его редактирования"
        ));

        log.info("Комментарий с ID {} обновлен пользователем {}", commentId, authorId);
        return mapper.toViewDto(updatedComment);
    }

    /**
     * Получает все комментарии для указанного поста.
     * <p>
     * Проверяет существование поста и возвращает список всех связанных с ним комментариев.
     * </p>
     *
     * @param postId идентификатор поста
     * @return список DTO комментариев для указанного поста
     * @throws EntityNotFoundException если пост с указанным идентификатором не найден
     */
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

    /**
     * Находит пост по идентификатору.
     *
     * @param postId идентификатор поста
     * @return найденный пост
     * @throws EntityNotFoundException если пост с указанным идентификатором не найден
     */
    private Post findPostById(Long postId) {
        return postRepository.findPostOrThrow(postId);
    }

    /**
     * Находит комментарий по идентификаторам поста и комментария.
     *
     * @param postId идентификатор поста
     * @param commentId идентификатор комментария
     * @return найденный комментарий
     * @throws EntityNotFoundException если комментарий с указанным идентификатором не найден
     */
    private Comment findCommentById(Long postId, Long commentId) {
        return commentRepository.findByIdAndPostIdOrThrow(postId, commentId);
    }
}