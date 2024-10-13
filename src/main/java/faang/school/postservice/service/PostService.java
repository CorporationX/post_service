package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.enums.AuthorType;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.messaging.NewPostPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    @Value("${spell-checker.batch-size}")
    private int correcterBatchSize;

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;
    private final NewPostPublisher newPostPublisher;
    private final BatchProcessService batchProcessService;
    private final ExecutorService schedulingThreadPoolExecutor;
    private final PostBatchService postBatchService;

    @Value("${post.publisher.butch-size}")
    private int batchSize;

    public PostDto createPost(PostDto postDto) {
        if (postDto.getAuthorType() == AuthorType.USER) {
            UserDto user = userServiceClient.getUser(postDto.getAuthorId());
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
        } else if (postDto.getAuthorType() == AuthorType.PROJECT) {
            ProjectDto project = projectServiceClient.getProject(postDto.getAuthorId());
            if (project == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
            }
        } else {
            throw new IllegalArgumentException("Invalid author type");
        }

        Post post = postMapper.toPost(postDto);
        Post savedPost = postRepository.save(post);
        PostDto result = postMapper.toPostDto(savedPost);

        newPostPublisher.publish(result);
        return result;
    }

    public PostDto publishPost(Long id) {
        Post post = getPostById(id);

        if (post.isPublished()) {
            throw new IllegalStateException("Post is already published");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);

        return postMapper.toPostDto(post);
    }

    public PostDto updatePost(Long id, PostDto postDto) {
        Post post = getPostById(id);

        if (!post.getAuthorId().equals(postDto.getAuthorId()) || !postDto.getAuthorType().equals(postDto.getAuthorType())) {
            throw new IllegalStateException("Cannot change author or author type of the post");
        }

        post.setContent(postDto.getContent());
        postRepository.save(post);

        return postMapper.toPostDto(post);
    }

    public void deletePost(Long id) {
        Post post = getPostById(id);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public PostDto getPost(Long id) {
        Post post = getPostById(id);
        return postMapper.toPostDto(post);
    }

    public List<PostDto> getUserDrafts(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toPostDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<PostDto> getProjectDrafts(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toPostDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<PostDto> getUserPublishedPosts(Long authorId) {
        return postRepository.findByAuthorIdWithLikes(authorId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toPostDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<PostDto> getProjectPublishedPosts(Long projectId) {
        return postRepository.findByProjectIdWithLikes(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toPostDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    private Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<PostDto> getAllPostsByHashtagId(String content, Pageable pageable) {
        return postRepository.findByHashtagsContent(content, pageable).map(postMapper::toPostDto);
    }

    @Transactional(readOnly = true)
    public Post getPostByIdInternal(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("'Post not in database' error occurred while fetching post"));
    }

    @Transactional
    public Post updatePostInternal(Post post) {
        return postRepository.save(post);
    }

    public List<CompletableFuture<Void>> publishScheduledPosts() {
        List<Post> readyToPublish = postRepository.findReadyToPublish();
        log.info("{} posts were found for scheduled publishing", readyToPublish.size());
        List<List<Post>> postBatches = partitionList(readyToPublish, batchSize);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Post> postBatch : postBatches) {
            postBatch.forEach(post -> {
                post.setPublished(true);
                post.setPublishedAt(LocalDateTime.now());
                log.info("Post with id '{}' prepared for scheduled publishing", post.getId());
            });
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> postBatchService.savePostBatch(postBatch), schedulingThreadPoolExecutor);
            futures.add(future);
        }
        return futures;
    }

    private List<List<Post>> partitionList(List<Post> list, int batchSize) {
        List<List<Post>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }

    @Transactional
    public void correctSpellingInUnpublishedPosts() {
        List<Post> unpublishedPosts = postRepository.findReadyToPublish();

        if (!unpublishedPosts.isEmpty()) {
            int batchSize = correcterBatchSize;
            List<List<Post>> batches = partitionList(unpublishedPosts, batchSize);

            List<CompletableFuture<Void>> futures = batches.stream()
                    .map(batchProcessService::processBatch)
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }
}
