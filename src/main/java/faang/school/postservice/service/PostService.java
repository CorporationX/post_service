package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.ModerationDictionary;
import faang.school.postservice.util.RedisPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final ResponsePostMapper responsePostMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final ModerationDictionary moderationDictionary;
    private final Integer batchSize;
    private final RedisPublisher redisPublisher;

    public PostService(PostRepository postRepository, ResponsePostMapper responsePostMapper,
                       UserServiceClient userServiceClient, ProjectServiceClient projectServiceClient,
                       ModerationDictionary moderationDictionary, @Value("${post.moderator.scheduler.batchSize}") Integer batchSize, RedisPublisher redisPublisher) {
        this.postRepository = postRepository;
        this.responsePostMapper = responsePostMapper;
        this.userServiceClient = userServiceClient;
        this.projectServiceClient = projectServiceClient;
        this.moderationDictionary = moderationDictionary;
        this.batchSize = batchSize;
        this.redisPublisher = redisPublisher;
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllDraftByAuthor(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllPublishedByAuthor(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllDraftByProject(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllPublishedByProject(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());

    }

    @Transactional
    public ResponsePostDto publish(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post is not found"));

        if (post.isPublished()) {
            throw new IllegalArgumentException("Can't publish already published post");
        }
        if (post.isDeleted()) {
            throw new IllegalArgumentException("Post has been deleted");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return responsePostMapper.toDto(post);
    }

    @Transactional
    public ResponsePostDto update(UpdatePostDto dto) {
        Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Post is not found"));

        post.setContent(dto.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        return responsePostMapper.toDto(post);
    }


    @Transactional(readOnly = true)
    public ResponsePostDto getById(Long id) {
        return responsePostMapper.toDto(
                postRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Post is not found"))
        );
    }

    @Transactional
    public ResponsePostDto softDelete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post is not found"));

        post.setDeleted(true);

        return responsePostMapper.toDto(post);
    }

    @Transactional
    public ResponsePostDto createDraft(CreatePostDto dto) {
        Post post = new Post();

        processOwner(dto, post);

        post.setContent(dto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setPublished(false);
        post.setDeleted(false);

        return responsePostMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public void verifyContent() {
        List<Post> posts = postRepository.findAllByVerifiedAtIsNull();
        List<List<Post>> grouped = new ArrayList<>();
        if (posts.size() > batchSize) {
            int i = 0;
            while (i < posts.size() / batchSize) {
                grouped.add(posts.subList(i, i + batchSize));
                i += batchSize;
            }
            if (i < posts.size()) {
                grouped.add(posts.subList(i, posts.size()));
            }
        } else {
            grouped.add(posts);
        }

        List<CompletableFuture<Void>> completableFutures = grouped.stream()
                .map(list -> CompletableFuture.runAsync(() -> verifySublist(list)))
                .toList();

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
    }

    public void banForOffensiveContent() {
        postRepository.findAllByVerifiedFalseAndVerifiedAtIsNotNull().stream()
                .collect(Collectors.groupingBy(Post::getAuthorId))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 5)
                .map(Map.Entry::getKey)
                .toList()
                .forEach(authorId -> redisPublisher.publishMessage("auto-banner", String.valueOf(authorId)));
    }

    private void verifySublist(List<Post> subList) {
        subList.forEach(post -> {
            post.setVerified(!moderationDictionary.containsBadWord(post.getContent()));
            post.setVerifiedAt(LocalDateTime.now());
        });
        postRepository.saveAll(subList);
    }

    private void processOwner(CreatePostDto dto, Post post) {
        if (dto.getAuthorId() != null && dto.getProjectId() != null) {
            throw new IllegalArgumentException("Both AuthorId and ProjectId can't be not null");
        }
        if (dto.getAuthorId() != null) {
            UserDto userDto = Objects.requireNonNull(userServiceClient.getUser(dto.getAuthorId()));
            post.setAuthorId(userDto.getId());
        }
        if (dto.getProjectId() != null) {
            ProjectDto projectDto = Objects.requireNonNull(projectServiceClient.getProject(dto.getProjectId()));
            post.setProjectId(projectDto.getId());
        }
    }

    public Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post with id " + postId + " was not found!"));
    }
}
