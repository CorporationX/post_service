package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.ScheduledPostPublicationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final ResourceService resourceService;
    private final ExecutorService scheduledPostExecutorService;
    private static final int BATCH_SIZE = 1000;

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
        return postMapper.toDto(postRepository.save(post));
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
        return postRepository.findPublishedByAuthor(userId).stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllPostsByProjectId(Long projectId) {
        return postRepository.findPublishedByProject(projectId).stream()
                .map(postMapper::toDto)
                .toList();
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
        List<List<Post>> batches = createBatches(postsToPublish);
        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(
                        () -> publishPosts(batch), scheduledPostExecutorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("Finished scheduled post publishing job");
    }

    private List<List<Post>> createBatches(List<Post> posts) {
        return IntStream.range(0, (posts.size() + BATCH_SIZE - 1) / BATCH_SIZE)
                .mapToObj(i -> posts.subList(i * BATCH_SIZE, Math.min((i + 1) * BATCH_SIZE, posts.size()))).toList();
    }

    private void publishPosts(List<Post> batch) {
        try {
            log.debug("Publishing {} posts", batch.size());
            LocalDateTime publishTime = LocalDateTime.now();
            batch.forEach(post -> {
                post.setPublished(true);
                post.setPublishedAt(publishTime);
            });
            postRepository.saveAll(batch);
            log.debug("Successfully published {} posts", batch.size());
        } catch (Exception e) {
            log.error("Error while publishing scheduled posts", e);
            throw new ScheduledPostPublicationException("Failed to publish scheduled posts", e.getCause());
        }
    }
}
