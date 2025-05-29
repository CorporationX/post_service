package faang.school.postservice.service.post;

import faang.school.postservice.config.moderation.ModerationConfig;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.PostValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Сервисный класс `PostService` для управления постами.
 * Обеспечивает создание, обновление, публикацию и удаление постов.
 *
 * <p>Основные методы:
 * <ul>
 *     <li>{@link #createDraft(PostCreateDto)} - создание черновика поста.</li>
 *     <li>{@link #publishPost(long)} - публикация поста.</li>
 *     <li>{@link #updatePost(PostUpdateDto, long)} - обновление поста.</li>
 *     <li>{@link #softDeletePost(long)} - мягкое удаление поста.</li>
 *     <li>{@link #getPost(long)} - получение поста по ID.</li>
 *     <li>{@link #getUserDrafts(long)} - получение черновиков пользователя.</li>
 *     <li>{@link #getProjectDrafts(long)} - получение черновиков проекта.</li>
 *     <li>{@link #getAuthorPublishedPosts(long)} - получение опубликованных постов автора.</li>
 *     <li>{@link #getProjectPublishedPosts(long)} - получение опубликованных постов проекта.</li>
 *     <li>{@link #moderateUnverifiedPost()} - модерирует посты на наличие нецензурнойм лексики.</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final PostModerationAsyncHandler postModerationAsyncHandler;
    private final ModerationConfig postModerationConfig;

    /**
     * Создает черновик поста на основе переданного DTO.
     *
     * @param postCreateDto DTO с данными для создания поста.
     * @return {@link PostViewDto} - DTO с данными созданного поста.
     */
    @Transactional
    public PostViewDto createDraft(@NotNull PostCreateDto postCreateDto) {
        postValidator.validateAuthorAndProject(postCreateDto);

        Post post = postMapper.createDtoToEntity(postCreateDto);
        post = postRepository.save(post);

        return postMapper.toViewDto(post);
    }

    /**
     * Публикует пост с указанным ID.
     *
     * @param postId ID поста для публикации.
     * @return {@link PostViewDto} - DTO с данными опубликованного поста.
     * @throws EntityNotFoundException если пост с указанным ID не найден.
     * @throws DataValidationException если пост уже опубликован.
     */
    @Transactional
    public PostViewDto publishPost(long postId) {
        Post publishPost = getPostEntity(postId);

        if (publishPost.isPublished()) {
            throw new DataValidationException("Post is already published");
        }

        publishPost.setPublished(true);
        publishPost.setPublishedAt(LocalDateTime.now());

        return postMapper.toViewDto(publishPost);
    }

    /**
     * Обновляет пост с указанным ID на основе переданного DTO.
     *
     * @param postUpdateDto DTO с данными для обновления поста.
     * @param postId ID поста для обновления.
     * @return {@link PostViewDto} - DTO с обновленными данными поста.
     * @throws EntityNotFoundException если пост с указанным ID не найден.
     */
    @Transactional
    public PostViewDto updatePost(@NotNull PostUpdateDto postUpdateDto, long postId) {
        Post oldPost = getPostEntity(postId);

        postMapper.update(postUpdateDto, oldPost);

        return postMapper.toViewDto(oldPost);
    }

    /**
     * Выполняет мягкое удаление поста с указанным ID.
     *
     * @param postId ID поста для удаления.
     * @return {@link PostViewDto} - DTO с данными удаленного поста.
     * @throws EntityNotFoundException если пост с указанным ID не найден.
     * @throws DataValidationException если пост уже удален.
     */
    @Transactional
    public PostViewDto softDeletePost(long postId) {
        Post post = getPostEntity(postId);

        if (post.isDeleted()) {
            throw new DataValidationException(String.format("Post with id %s is already deleted", postId));
        }
        post.setDeleted(true);

        return postMapper.toViewDto(post);
    }

    /**
     * Возвращает пост по указанному ID.
     *
     * @param postId ID поста.
     * @return {@link PostViewDto} - DTO с данными поста.
     * @throws EntityNotFoundException если пост с указанным ID не найден.
     */
    public PostViewDto getPost(long postId) {
        Post post = getPostEntity(postId);

        return postMapper.toViewDto(post);
    }

    /**
     * Возвращает список черновиков пользователя с указанным ID.
     *
     * @param userId ID пользователя.
     * @return {@link List<PostViewDto>} - список DTO с данными черновиков пользователя.
     */
    public List<PostViewDto> getUserDrafts(long userId) {
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted())
                .filter(post -> !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toViewDto)
                .toList();
    }

    /**
     * Возвращает список черновиков проекта с указанным ID.
     *
     * @param projectId ID проекта.
     * @return {@link List<PostViewDto>} - список DTO с данными черновиков проекта.
     */
    public List<PostViewDto> getProjectDrafts(long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted())
                .filter(post -> !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toViewDto)
                .toList();
    }

    /**
     * Возвращает список опубликованных постов автора с указанным ID и лайками.
     *
     * @param userId ID автора.
     * @return {@link List<PostViewDto>} - список DTO с данными опубликованных постов автора.
     */
    public List<PostViewDto> getAuthorPublishedPosts(long userId) {
        return postRepository.findByAuthorIdWithLikes(userId).stream()
                .filter(post -> !post.isDeleted())
                .filter(Post::isPublished)
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toViewDto)
                .toList();
    }

    /**
     * Возвращает список опубликованных постов проекта с указанным ID и лайками.
     *
     * @param projectId ID проекта.
     * @return {@link List<PostViewDto>} - список DTO с данными опубликованных постов проекта.
     */
    public List<PostViewDto> getProjectPublishedPosts(long projectId) {
        return postRepository.findByProjectIdWithLikes(projectId).stream()
                .filter(post -> !post.isDeleted())
                .filter(Post::isPublished)
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toViewDto)
                .toList();
    }

    /**
     * Возвращает пост по его идентификатору.
     *
     * @param postId Идентификатор поста.
     * @return Найденный пост.
     * @throws EntityNotFoundException Если пост с указанным идентификатором не найден.
     */
    public Post getPostEntity(long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Post not found with id: %s", postId)));
    }

    /**
     * Запускает процесс модерации всех неверифицированных постов.
     * <p>
     * Метод выполняет следующие действия:
     * <ol>
     *   <li>Находит все посты без даты верификации</li>
     *   <li>Разбивает их на пакеты указанного размера</li>
     *   <li>Запускает асинхронную проверку каждого пакета</li>
     *   <li>Ожидает завершения всех проверок</li>
     * </ol>
     */
    public void moderateUnverifiedPost() {
        log.info("Moderation of unverified posts started");
        List<Post> unverifiedPosts = postRepository.findAllByVerifiedAtIsNull();

        if (unverifiedPosts.isEmpty()) {
            return;
        }

        List<List<Post>> batches = ListUtils.partition(unverifiedPosts, postModerationConfig.getBatchSize());

        List<CompletableFuture<Void>> moderationTasks = batches.stream()
                .map(postModerationAsyncHandler::checkForProfanity)
                .toList();

        CompletableFuture.allOf(moderationTasks.toArray(new CompletableFuture[0])).join();
        log.info("Moderation completed successfully. Total posts processed: {}", unverifiedPosts.size());
    }
}