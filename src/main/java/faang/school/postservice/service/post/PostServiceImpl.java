package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.client.languagetool.LanguageToolClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.languagetool.LanguageToolResponseDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.publisher.post.RedisPostCreateEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.util.LanguageTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    @Value("${scheduler.batch-size}")
    private int batchSize;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final LanguageToolClient languageToolClient;
    private final ResourceService resourceService;
    private final UserContext userContext;
    private final PostActionService postActionService;
    private final ExecutorService scheduledPostExecutorService;
    private final faang.school.postservice.service.cache.PostCachePort postCachePort;
    private final RedisPostCreateEventPublisher redisPostCreateEventPublisher;

    @Override
    @Transactional
    public PostDto createDraft(PostDto dto) {
        validateAuthor(dto.authorId(), dto.projectId());
        Post post = postMapper.toEntity(dto);
        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public PostDto createDraft(PostDto dto, List<MultipartFile> files) {
        validateAuthor(dto.authorId(), dto.projectId());
        Post post = postMapper.toEntity(dto);

        List<Resource> resources = resourceService.uploadResources(files, 0);
        resources.forEach(resource -> resource.setPost(post));
        post.setResources(resources);

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public PostDto publishPost(Long postId) {
        Post post = getExistingPost(postId);
        if (post.isPublished()) {
            throw new DataValidationException("Post with id=" + postId + " is already published");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);

        faang.school.postservice.service.cache.model.PostCacheDto cacheDto =
                faang.school.postservice.service.cache.mapper.PostCacheMapper.fromEntity(updatedPost);
        postCachePort.put(cacheDto);

        redisPostCreateEventPublisher.publish(postMapper.toPostCreateEventDto(updatedPost));

        return postMapper.toDto(updatedPost);
    }

    @Override
    @Transactional
    public PostDto updatePost(Long postId, PostDto dto) {
        Post post = getExistingPost(postId);
        validateAuthorUnchanged(post, dto);
        post.setContent(dto.content());
        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public PostDto updatePost(Long postId, PostDto dto, List<MultipartFile> newFiles) {
        Post post = getExistingPost(postId);
        validateAuthorUnchanged(post, dto);

        post.setContent(dto.content());

        List<Resource> currentResources = post.getResources();

        List<Resource> finalResources;
        if (dto.resourceKeys() == null) {
            finalResources = new ArrayList<>(currentResources);
        } else {
            List<String> toKeepKeys = dto.resourceKeys();
            List<Resource> toDeleteResources = currentResources.stream()
                    .filter(resource -> !toKeepKeys.contains(resource.getKey()))
                    .toList();
            resourceService.deleteResources(toDeleteResources);
            finalResources = currentResources.stream()
                    .filter(resource -> toKeepKeys.contains(resource.getKey()))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        List<Resource> newResources = resourceService.uploadResources(newFiles, finalResources.size());
        newResources.forEach(resource -> resource.setPost(post));
        finalResources.addAll(newResources);

        currentResources.clear();
        currentResources.addAll(finalResources);

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        Post post = getExistingPost(postId);
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getPost(Long postId) {
        Post post = getExistingPost(postId);
        Long viewerId = userContext.getUserId();
        postActionService.registerPostView(post.getId(), post.getAuthorId(), viewerId);
        return postMapper.toDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllDraftsByAuthorId(Long userId) {
        return postRepository.findDraftsByAuthor(userId).stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllDraftsByProjectId(Long projectId) {
        return postRepository.findDraftsByProject(projectId).stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllPostsByAuthorId(Long userId) {
        Long viewerId = userContext.getUserId();
        return postRepository.findPublishedByAuthor(userId).stream()
                .peek(post -> postActionService.registerPostView(post.getId(), post.getAuthorId(), viewerId))
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllPostsByProjectId(Long projectId) {
        Long viewerId = userContext.getUserId();
        return postRepository.findPublishedByProject(projectId).stream()
                .peek(post -> postActionService.registerPostView(post.getId(), post.getAuthorId(), viewerId))
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void correctContentDraftPostsByLanguageToolAI() {
        List<Post> posts = postRepository.findUnpublishedAndNotDeleted();

        for (Post post : posts) {
            try {
                LanguageToolResponseDto languageToolResponseDto = languageToolClient.correctText(post.getContent());
                String corrected = LanguageTool.applyCorrectText(post.getContent(), languageToolResponseDto);
                post.setContent(corrected);
                postRepository.save(post);
            } catch (Exception e) {
                log.warn("Failed to correct post {}: {}", post.getId(), e.getMessage());
            }
        }
    }

    public Post getExistingPost(Long id) {
        return postRepository.findById(id)
                .filter(post -> !post.isDeleted())
                .orElseThrow(() ->
                        new PostNotFoundException("Post with id=" + id + " not found or has been deleted"));
    }

    private void validateAuthorUnchanged(Post post, PostDto dto) {
        if (!post.getAuthorId().equals(dto.authorId()) ||
                (post.getProjectId() != null && !post.getProjectId().equals(dto.projectId()))) {
            throw new DataValidationException("Post author cannot be changed (postId=" + post.getId() + ")");
        }
    }

    private void validateAuthor(Long authorId, Long projectId) {
        if ((authorId == null && projectId == null) || (authorId != null && projectId != null)) {
            throw new DataValidationException("Author must be either a user or a project, but not both or neither");
        }
        if (authorId != null) {
            userServiceClient.getUser(authorId);
        } else {
            projectServiceClient.getProject(projectId);
        }
    }

    @Override
    @Transactional
    public void publishScheduledPosts() {
        log.info("Starting scheduled post publishing job");
        List<Post> postsToPublish = postRepository.findReadyToPublish();
        if (postsToPublish.isEmpty()) {
            log.info("No scheduled posts found to publish");
            return;
        }
        log.info("Found {} scheduled posts to publish", postsToPublish.size());
        List<List<Post>> batches = ListUtils.partition(postsToPublish, batchSize);
        int successfulBatches = 0;
        int failedBatches = 0;
        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(
                        () -> publishPosts(batch), scheduledPostExecutorService))
                .toList();

        for (CompletableFuture<Void> future : futures) {
            try {
                future.join();
                successfulBatches++;
            } catch (Exception e) {
                failedBatches++;
                log.error("Failed to publish batch", e);
            }
        }

        log.info("Finished scheduled post publishing job. Successful: {}, Failed: {}",
                successfulBatches, failedBatches);
    }

    private void publishPosts(List<Post> batch) {
        log.debug("Publishing {} posts", batch.size());
        LocalDateTime publishTime = LocalDateTime.now();
        batch.forEach(post -> {
            post.setPublished(true);
            post.setPublishedAt(publishTime);
        });
        postRepository.saveAll(batch);
        log.debug("Successfully published {} posts", batch.size());
    }
}

