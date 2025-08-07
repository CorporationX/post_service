package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.messaging.producer.EventProducerService;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.message.Event;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.filter.FilterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    private final EventProducerService<PostViewDto> postEventProducerService;

    @Override
    @Transactional
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
        postEventProducerService.produce(Event.POST_CREATE, view);
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
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Override
    public PostViewDto update(long postId, PostUpdateDto updateDto) {
        var currentUserId = userContext.getUserId();
        var post = getPostById(postId);
        checkUserAccess(currentUserId, post);
        if (post.isDeleted()) {
            throw new ForbiddenException("Пост удален, нельзя редактировать");
        }
        postMapper.update(updateDto, post);
        post = postRepository.save(post);
        return postMapper.toViewDto(post);
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
        post.setDeleted(true);
        postRepository.save(post);
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
            posts = postRepository.findByAuthorId(filterDto.authorId());
        } else {
            posts = postRepository.findByProjectId(filterDto.authorId());
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
}
