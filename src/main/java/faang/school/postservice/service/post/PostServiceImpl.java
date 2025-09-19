package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.redis.entity.PostRedis;
import faang.school.postservice.config.redis.entity.UserRedis;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.messaging.dto.PostPublishEvent;
import faang.school.postservice.messaging.dto.PostUpdatedEvent;
import faang.school.postservice.messaging.producer.EventProducer;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserRedisRepository;
import faang.school.postservice.service.filter.FilterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Реализация сервиса для управления публикациями (постами).
 * <p>
 * Предоставляет функционал для:
 * <ul>
 *   <li>Создания новых постов от имени пользователя или проекта</li>
 *   <li>Публикации постов</li>
 *   <li>Обновления и удаления постов</li>
 *   <li>Получения постов по идентификатору и с применением фильтрации</li>
 * </ul>
 * <p>
 *
 * @author Myrza
 * @since 25.07.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private static final String USER_HAS_NO_ACCESS_TO_CREATE_POST =
            "Недостаточно прав для создания поста от имени пользователя с id ";

    private static final String USER_HAS_NO_ACCESS_TO_POST =
            "Пользователь не имеет право на данный пост";
    private final PostRepository postRepository;
    private final UserServiceClient userClient;
    private final ProjectServiceClient projectClient;
    private final UserContext userContext;
    private final PostMapper postMapper;
    private final FilterService<Post, PostFilterDto> filterService;
    @Qualifier("postCreateEventProducer")
    private final EventProducer<PostViewDto> postCreateProducer;
    private final EventProducer<PostUpdatedEvent> postUpdatedEventProducer;
    @Qualifier("postDeleteEventProducer")
    private final EventProducer<PostViewDto> postDeleteProducer;
    private final ExecutorService executor;
    private final PostRedisRepository postRedisRepository;
    private final UserRedisRepository userRedisRepository;
    @Qualifier("postPublishEventProducer")
    private final EventProducer<PostPublishEvent> postPublishProducer;

    @Override
    public PostViewDto create(PostCreateDto createDto) {
        var currentUserId = userContext.getUserId();
        createDto.validate();
        var authorIsUser = createDto.authorId() != null;
        if (authorIsUser) {
            if (currentUserId != createDto.authorId()) {
                log.warn("Пользователь с id {} пытается создать публикацию от имени пользователя с id {}",
                        currentUserId, createDto.authorId());
                throw new ForbiddenException(USER_HAS_NO_ACCESS_TO_CREATE_POST + createDto.authorId());
            }
            userClient.getUser(createDto.authorId());
        } else {
            projectClient.getProject(createDto.projectId());
        }

        var post = postMapper.toEntity(createDto);
        post = postRepository.save(post);
        var view = postMapper.toViewDto(post);
        sendEventInNewTransaction(postCreateProducer, view);
        return view;
    }

    @Override
    @Transactional
    public void publish(long postId) {
        var currentUserId = userContext.getUserId();
        var post = getPostById(postId);
        checkUserAccess(currentUserId, post);
        if (post.isDeleted()) {
            throw new ForbiddenException("Пост удален, нельзя опубликовать");
        }
        if (post.isPublished()) {
            throw new ForbiddenException("Пост уже опубликован");
        }
        var oldView = postMapper.toViewDto(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);

        savePostInRedisAndKafka(postMapper.toRedis(post));

        var newView = postMapper.toViewDto(post);
        var event = new PostUpdatedEvent(oldView, newView);
        sendEventInNewTransaction(postUpdatedEventProducer, event);
    }

    private void savePostInRedisAndKafka(PostRedis postRedis) {
        log.info("Кэширование поста и его автора в редис и отправка события в кафку");
        Long authorId = postRedis.getAuthorId();
        UserDto author = userClient.getUser(authorId);
        userRedisRepository.save(new UserRedis(author.id(), author.username()));
        postRedisRepository.save(postRedis);

        PostPublishEvent postPublishEvent = new PostPublishEvent(postRedis.getAuthorId(), postRedis.getId(), author.followersIds());
        sendEventInNewTransaction(postPublishProducer, postPublishEvent);
    }

    @Override
    public PostViewDto update(long postId, PostUpdateDto updateDto) {
        var currentUserId = userContext.getUserId();
        var post = getPostById(postId);
        checkUserAccess(currentUserId, post);
        if (post.isDeleted()) {
            throw new ForbiddenException("Пост удален, нельзя редактировать");
        }
        var oldPostDto = postMapper.toViewDto(post);
        postMapper.update(updateDto, post);
        post = postRepository.save(post);
        var newPostDto = postMapper.toViewDto(post);
        var postUpdatedEvent = new PostUpdatedEvent(oldPostDto, newPostDto);
        sendEventInNewTransaction(postUpdatedEventProducer, postUpdatedEvent);
        return newPostDto;
    }

    @Override
    @Transactional
    public void delete(long postId) {
        var currentUserId = userContext.getUserId();
        var post = getPostById(postId);
        checkUserAccess(currentUserId, post);
        if (post.isDeleted()) {
            throw new ForbiddenException("Пост уже удален");
        }
        var view = postMapper.toViewDto(post);
        post.setDeleted(true);
        postRepository.save(post);
        sendEventInNewTransaction(postDeleteProducer, view);
    }

    @Override
    public PostViewDto getById(long postId) {
        var post = getPostById(postId);
        return postMapper.toViewDto(post);
    }

    @Override
    public List<PostViewDto> getList(PostFilterDto filterDto) {
        List<Post> posts = null;
        if (filterDto.authorIsUser()) {
            posts = postRepository.findByAuthorIdWithLikes(filterDto.authorId());
        } else {
            posts = postRepository.findByProjectIdWithLikes(filterDto.authorId());
        }
        posts = filterService.getFilteredList(posts, filterDto);
        return postMapper.toViewDtoList(posts);
    }

    private Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Публикация с id " + postId + " не найдена")
                );
    }

    private void checkUserAccess(Long userId, Post post) {
        if (post.getAuthorId() != null && !Objects.equals(userId, post.getAuthorId())) {
            log.warn("Пользователь с id {} не имеет прав к редактированию публикации {}",
                    userId, post.getId());
            throw new ForbiddenException(USER_HAS_NO_ACCESS_TO_POST);
        }
    }

    private <E> void sendEvent(EventProducer<E> producer, E event) {
        CompletableFuture.runAsync(() -> producer.send(event), executor)
                .exceptionally(ex -> {
                    log.error("Ошибка публикации события {}", ex.getMessage(), ex);
                    return null;
                });
    }

    private <E> void sendEventInNewTransaction(EventProducer<E> producer, E event) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            sendEvent(producer, event);
            return;
        }
        var synchronizer = new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sendEvent(producer, event);
            }
        };
        TransactionSynchronizationManager.registerSynchronization(synchronizer);
    }
}
