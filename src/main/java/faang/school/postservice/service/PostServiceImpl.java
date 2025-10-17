package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Override
    public PostResponseDto createDraft(CreatePostRequestDto dto) {
        log.info("Создание черновика: authorId={}, projectId={}", dto.authorId(), dto.projectId());

        if (!dto.isExactlyOneAuthor()) {
            log.warn("Ошибка создания: должен быть указан ровно один автор (authorId XOR projectId)");
            throw new IllegalArgumentException("Должен быть указан ровно один автор: authorId или projectId");
        }
        if (dto.content() == null || dto.content().isBlank()) {
            log.warn("Ошибка создания: content пустой");
            throw new IllegalArgumentException("content не должен быть пустым");
        }

        try {
            if (dto.authorId() != null) {
                userServiceClient.getUser(dto.authorId());
            } else {
                projectServiceClient.getProject(dto.projectId());
            }
        } catch (FeignException ex) {
            log.warn("Автор не найден во внешнем сервисе: {}", ex.getMessage());
            throw new IllegalArgumentException("Автор не найден во внешнем сервисе");
        }

        Post entity = postMapper.toEntity(dto);
        entity.setDeleted(false);
        entity.setPublished(false);
        entity.setPublishedAt(null);

        Post saved = postRepository.save(entity);
        log.info("Черновик создан id={}", saved.getId());
        return postMapper.toDto(saved);
    }
    @Override
    public PostResponseDto publish(long id) {
        log.info("Публикация поста id={}", id);
        Post post = getExisting(id);

        if (post.isDeleted()) {
            log.warn("Попытка публикации удалённого поста id={}", id);
            throw new IllegalStateException("Нельзя публиковать удалённый пост");
        }
        if (post.isPublished()) {
            log.warn("Попытка повторной публикации поста id={}", id);
            throw new IllegalStateException("Пост уже опубликован");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        Post saved = postRepository.save(post);

        log.info("Пост опубликован id={} в {}", id, saved.getPublishedAt());
        return postMapper.toDto(saved);
    }

    @Override
    public PostResponseDto update(long id, UpdatePostRequestDto dto) {
        log.info("Обновление поста id={}", id);
        Post post = getExisting(id);

        if (post.isDeleted()) {
            log.warn("Попытка обновления удалённого поста id={}", id);
            throw new IllegalStateException("Нельзя обновлять удалённый пост");
        }

        if (dto.content() != null) {
            if (dto.content().isBlank()) {
                log.warn("Ошибка обновления поста id={}: пустой content", id);
                throw new IllegalArgumentException("content не должен быть пустым");
            }
            post.setContent(dto.content());
        }

        Post saved = postRepository.save(post);
        log.info("Пост id={} обновлён", id);
        return postMapper.toDto(saved);
    }

    @Override
    public void softDelete(long id) {
        log.info("Мягкое удаление поста id={}", id);
        Post post = getExisting(id);

        if (!post.isDeleted()) {
            post.setDeleted(true);
            post.setPublished(false);
            postRepository.save(post);
            log.info("Пост id={} помечен как удалённый (published=false)", id);
        } else {
            log.debug("Пост id={} уже был удалён ранее", id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto getById(long id) {
        log.debug("Получение поста по id={}", id);
        return postMapper.toDto(getExisting(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getDraftsByUser(long userId) {
        log.debug("Черновики пользователя userId={}", userId);
        return postRepository.findByAuthorId(userId).stream()
                .filter(p -> !p.isDeleted() && !p.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getDraftsByProject(long projectId) {
        log.debug("Черновики проекта projectId={}", projectId);
        return postRepository.findByProjectId(projectId).stream()
                .filter(p -> !p.isDeleted() && !p.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPublishedByUser(long userId) {
        log.debug("Опубликованные посты пользователя userId={}", userId);
        return postRepository.findByAuthorId(userId).stream()
                .filter(p -> !p.isDeleted() && p.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPublishedByProject(long projectId) {
        log.debug("Опубликованные посты проекта projectId={}", projectId);
        return postRepository.findByProjectId(projectId).stream()
                .filter(p -> !p.isDeleted() && p.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    private Post getExisting(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пост не найден id={}", id);
                    return new IllegalArgumentException("Пост не найден: " + id);
                });
    }
}