package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.TextCheck;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.LanguageToolConfig;
import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.text.MatchDto;
import faang.school.postservice.dto.text.TextResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ProjectNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final LanguageToolConfig languageToolConfig;
    private final TextCheck textCheck;
    private static final int MAX_RETRY_ATTEMPTS = 4;
    private static final long INITIAL_DELAY_MS = 1000L;
    private static final long SECOND_DELAY_MS = 3000L;
    private static final long THIRD_DELAY_MS = 4000L;
    private static final long FOURTH_DELAY_MS = 5000L;

    @Override
    public PostResponseDto createDraft(CreatePostRequestDto dto) {
        log.info("Creating draft: authorId={}, projectId={}", dto.authorId(), dto.projectId());

        if (!dto.isExactlyOneAuthor()) {
            log.warn("Draft creation error: exactly one author must be specified (authorId XOR projectId)");
            throw new IllegalArgumentException("Exactly one author must be specified: authorId or projectId");
        }
        if (dto.content() == null || dto.content().isBlank()) {
            log.warn("Draft creation error: content is empty");
            throw new IllegalArgumentException("content must not be empty");
        }

        try {
            if (dto.authorId() != null) {
                ResponseEntity<UserDto> resp = userServiceClient.getUser(dto.authorId());
                if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new UserNotFoundException(dto.authorId());
                }
            } else {
                ResponseEntity<ProjectDto> resp = projectServiceClient.getProject(dto.projectId());
                if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new ProjectNotFoundException(dto.projectId());
                }
            }
        } catch (FeignException.NotFound ex) {
            log.warn("Author not found in external service: {}", ex.getMessage());
            throw new IllegalArgumentException("Author not found in external service");
        }

        Post draft = postMapper.toEntity(dto);

        if (draft.getScheduledAt() == null) {
            draft.setScheduledAt(LocalDateTime.now());
        }
        Post saved = postRepository.save(draft);
        log.info("Draft created id={}", saved.getId());
        return postMapper.toDto(saved);
    }

    @Override
    public PostResponseDto publish(long id) {
        log.info("Publishing post id={}", id);
        Post post = getPostEntityById(id);

        if (post.isDeleted()) {
            log.warn("Attempt to publish a deleted post id={}", id);
            throw new IllegalStateException("Cannot publish a deleted post");
        }
        if (post.isPublished()) {
            log.warn("Attempt to republish an already published post id={}", id);
            throw new IllegalStateException("Post is already published");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        Post saved = postRepository.save(post);

        log.info("Post published id={} at {}", id, saved.getPublishedAt());
        return postMapper.toDto(saved);
    }

    @Override
    public PostResponseDto update(long id, UpdatePostRequestDto dto) {
        log.info("Updating post id={}", id);
        Post post = getPostEntityById(id);

        if (post.isDeleted()) {
            log.warn("Attempt to update a deleted post id={}", id);
            throw new IllegalStateException("Cannot update a deleted post");
        }

        if (dto.content() != null) {
            if (dto.content().isBlank()) {
                log.warn("Post update error id={}: content is empty", id);
                throw new IllegalArgumentException("content must not be empty");
            }
            postMapper.updateEntityFromDto(dto, post);
            post.setUpdatedAt(LocalDateTime.now());
        }

        Post saved = postRepository.save(post);
        log.info("Post id={} updated", id);
        return postMapper.toDto(saved);
    }

    @Override
    public void softDelete(long id) {
        log.info("Soft deleting post id={}", id);
        Post post = getPostEntityById(id);

        if (!post.isDeleted()) {
            post.setDeleted(true);
            post.setPublished(false);
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.save(post);
            log.info("Post id={} marked as deleted (published=false)", id);
        } else {
            log.debug("Post id={} was already deleted earlier", id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto getById(long id) {
        log.debug("Fetching post by id={}", id);
        return postMapper.toDto(getPostEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getDraftsByUser(long userId) {
        log.debug("Fetching drafts for userId={}", userId);
        return postRepository.findByAuthorId(userId).stream()
                .filter(p -> !p.isDeleted() && !p.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getDraftsByProject(long projectId) {
        log.debug("Fetching drafts for projectId={}", projectId);
        return postRepository.findByProjectId(projectId).stream()
                .filter(p -> !p.isDeleted() && !p.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPublishedByUser(long userId) {
        log.debug("Fetching published posts for userId={}", userId);
        return postRepository.findByAuthorId(userId).stream()
                .filter(p -> !p.isDeleted() && p.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPublishedByProject(long projectId) {
        log.debug("Fetching published posts for projectId={}", projectId);
        return postRepository.findByProjectId(projectId).stream()
                .filter(p -> !p.isDeleted() && p.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPostEntityById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Post not found with id={}", id);
                    return new IllegalArgumentException("Post not found with id: " + id);
                });
    }

    @Override
    @Transactional
    public void processTextChecking() {
        List<Post> unpublishedPosts = postRepository.findReadyToPublish(); 

        for (Post post : unpublishedPosts) {
            try { 
                TextResponseDto response = checkTextWithRetry(post.getContent());

                if (response == null) {
                    log.warn("Empty response from LanguageTool for post id={}", post.getId());
                    continue; 
                }
                
                String correctedText = applyCorrections(post.getContent(), response);
                
                if (!correctedText.equals(post.getContent())) {
                   
                    UpdatePostRequestDto updateDto = UpdatePostRequestDto.builder()
                            .content(correctedText)
                            .build();
                    update(post.getId(), updateDto);
                }
            } catch (Exception e) {
                log.error("Failed to check text for post with id={}", post.getId(), e);
            }
        }
    }

    public TextResponseDto checkTextWithRetry(String text) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                log.info("Checking text with LanguageTool, attempt {}", attempt);

                RestTemplate restTemplate = new RestTemplate();
                String url = "https://api.languagetool.org/v2/check?text=" +
                        URLEncoder.encode(text, StandardCharsets.UTF_8) +
                        "&language=" + languageToolConfig.getLanguage();

                return restTemplate.getForObject(url, TextResponseDto.class);

            } catch (Exception e) {
                if (attempt == MAX_RETRY_ATTEMPTS) {
                    log.error("All {} attempts failed for text correction", MAX_RETRY_ATTEMPTS);
                    throw e;
                }

                applyCustomBackoff(attempt);
            }
        }
        throw new RuntimeException("Unexpected error in retry logic");
    }
    //for PR
    private void applyCustomBackoff(int attempt) {
        long delay = switch (attempt) {
            case 1 -> INITIAL_DELAY_MS;
            case 2 -> SECOND_DELAY_MS;     
            case 3 -> THIRD_DELAY_MS;      
            case 4 -> FOURTH_DELAY_MS;
            default -> FOURTH_DELAY_MS;
        };

        try {
            log.info("Custom backoff: waiting {} ms before attempt {}", delay, attempt + 1);
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Backoff interrupted", ie);
        }
    }

    String applyCorrections(String originalText, TextResponseDto response) {
        if (response.matches() == null || response.matches().isEmpty()) {
            return originalText;
        }

        int previousEnd = 0;
        StringBuilder correctedText = new StringBuilder();

        for (MatchDto match : response.matches()) {
            if (match.replacements() != null && !match.replacements().isEmpty()) {
                String replacement = match.replacements().get(0).value();
                int offset = match.offset();
                int length = match.length();

                if (offset >= 0 && offset <= originalText.length() &&
                        offset + length <= originalText.length()) {
                    correctedText.append(originalText, previousEnd, offset);
                    correctedText.append(replacement);
                    previousEnd = offset + length;
                }
            }
        }
        if (previousEnd < originalText.length()) {
            correctedText.append(originalText.substring(previousEnd));
        }
        return correctedText.toString();
    }
}