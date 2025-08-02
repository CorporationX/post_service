package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса для работы с постами.
 *
 * @author Linempy
 * @see PostService
 * @since 25.07.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ProjectServiceClient projectClient;
    private final PostMapper mapper;
    private final UserContext context;

    @Override
    @Transactional
    public PostViewDto create(PostCreateDto dto) {
        Long currentUserId = context.getUserId();

        AuthorInfo authorInfo = resolveAuthor(dto, currentUserId);
        Post post = mapper.toEntity(dto);

        post.setAuthorId(authorInfo.authorId());
        post.setProjectId(authorInfo.projectId());

        post = postRepository.save(post);
        log.info("Пост id={} был создан (Автор: userId={} projectId={}",
                post.getId(), authorInfo.authorId(), authorInfo.projectId());
        return mapper.toViewDto(post);
    }

    @Override
    @Transactional
    public void publication(Long id) {
        Post post = postRepository.findPostOrThrow(id);

        if (post.isPublished()) {
            throw new ForbiddenException("Пост уже опубликован");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        log.info("Пост id={} был опубликован в {}", post.getId(), post.getPublishedAt());
        postRepository.save(post);
    }

    @Override
    @Transactional
    public PostViewDto update(Long id, PostUpdateDto dto) {
        Post post = postRepository.findPostOrThrow(id);

        mapper.update(post, dto);
        post = postRepository.save(post);
        log.info("Пост id={} был обновлен", post.getId());
        return mapper.toViewDto(post);
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        Post post = postRepository.findPostOrThrow(id);

        if (post.isDeleted()) {
            throw new ForbiddenException("Пост уже был удален");
        }

        post.setDeleted(true);
        log.info("Пост id={} был мягко удален", post.getId());
        postRepository.save(post);
    }

    @Override
    public PostViewDto getById(Long id) {
        Post post = postRepository.findPostOrThrow(id);
        return mapper.toViewDto(post);
    }

    @Override
    public List<PostViewDto> findByFilter(PostFilterDto filterDto) {
        List<Post> posts = postRepository.findByFilter(filterDto);
        return posts.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    private AuthorInfo resolveAuthor(PostCreateDto dto, Long currentUserId) {
        boolean hasAuthorId = dto.authorId() != null;
        boolean hasProjectId = dto.projectId() != null;

        if (hasAuthorId && hasProjectId) {
            throw new ForbiddenException("Должен быть указан только один идентификатор автора");
        }

        if (hasAuthorId) {
            if (!dto.authorId().equals(currentUserId)) {
                throw new ForbiddenException("Нельзя публиковать от чужого имени!");
            }
            return new AuthorInfo(currentUserId, null);
        }

        if (hasProjectId) {
            ProjectDto project = projectClient.getProject(dto.projectId());
            if (!project.participants().contains(currentUserId)) {
                throw new ForbiddenException("Вы не состоите в проекте!");
            }
            return new AuthorInfo(currentUserId, project.id());
        }

        throw new DataValidationException("Не указан идентификатор автора");
    }
}