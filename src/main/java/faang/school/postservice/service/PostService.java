package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.ProjectDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.moderator.PostModerationDictionary;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostValidator postValidator;
    private final PostRepository postRepository;
    private final AsyncPostPublishService asyncPostPublishService;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;
    private final PostModerationDictionary postModerationDictionary;
    private final JdbcTemplate jdbcTemplate;
    @Lazy
    private final ResourceService resourceService;
    private final  TransactionTemplate transactionTemplate;

    @Value("${scheduler.post-publisher.size_batch}")
    private int sizeSublist;

    @Value("${scheduler.moderation.post.batch_size}")
    int postBatchSize;

    public PostDto createDraftPost(PostDto postDto, @Nullable MultipartFile file) {
        UserDto author = null;
        ProjectDto project = null;

        if (postDto.getAuthorId() != null) {
            author = userServiceClient.getUser(postDto.getAuthorId());
        } else if (postDto.getProjectId() != null) {
            project = projectServiceClient.getProject(postDto.getProjectId());
        }
        postValidator.validateAuthorExists(author, project);
        if (file != null) {
            resourceService.addResource(postDto.getId(), file);
        }
        return savePost(postDto);
    }

    private PostDto savePost(PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        post.setVerified(false);
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto publishPost(long id) {
        Post post = findById(id);
        postValidator.validateIsNotPublished(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto updatePost(UpdatePostDto postDto, long id) {
        Post post = findById(id);
        post.setContent(postDto.getContent());

        return postMapper.toDto(postRepository.save(post));
    }

    public void deletePost(long id) {
        Post post = findById(id);
        if (post.isDeleted()) {
            throw new DataValidationException("Пост уже удален");
        } else {
            post.setDeleted(true);
            postRepository.save(post);
        }
    }

    public PostDto getPost(long id) {
        Post post = findById(id);
        return postMapper.toDto(post);
    }

    public List<PostDto> getDraftsByUser(long userId) {
        List<Post> foundedPosts = postRepository.findByAuthorId(userId);
        return getSortedDrafts(foundedPosts);
    }

    public List<PostDto> getDraftsByProject(long projectId) {
        List<Post> foundedPosts = postRepository.findByProjectId(projectId);
        return getSortedDrafts(foundedPosts);
    }

    public List<PostDto> getPublishedPostsByUser(long userId) {
        List<Post> foundedPosts = postRepository.findByAuthorIdWithLikes(userId);
        return getSortedPublished(foundedPosts);
    }

    public List<PostDto> getPublishedPostsByProject(long projectId) {
        List<Post> foundedPosts = postRepository.findByProjectIdWithLikes(projectId);
        return getSortedPublished(foundedPosts);
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new faang.school.postservice.exception.DataValidationException("Post was not found"));
    }

    @Transactional
    public void publishScheduledPosts() {
        log.info("Started publish posts from scheduler");
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Post> postsToPublish = postRepository.findReadyToPublish();
        if (!postsToPublish.isEmpty()) {
            log.info("Size of posts list publish is {}", postsToPublish.size());
            List<List<Post>> subLists = ListUtils.partition(postsToPublish, sizeSublist);
            subLists.forEach(asyncPostPublishService::publishPost);
            log.info("Finished publish all posts at {}", currentDateTime);
        } else {
            log.info("Unpublished posts at {} not found", currentDateTime);
        }
    }

    @Transactional
    public void moderatePosts() {
        List<Post> posts = postRepository.findAllByVerifiedDateIsNull();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < posts.size(); i += postBatchSize) {
            final int startIndex = i;
            executorService.submit(() -> {
                transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                transactionTemplate.execute(status -> {
                    final List<Post> batch = posts.subList(startIndex, Math.min(startIndex + postBatchSize, posts.size()));
                    jdbcTemplate.batchUpdate("UPDATE post SET verified = ?, verified_date = ? WHERE id = ?",
                            new BatchPreparedStatementSetter() {
                                @Override
                                public void setValues(PreparedStatement ps, int j) throws SQLException {
                                    Post post = batch.get(j);
                                    boolean containsForbiddenWords = postModerationDictionary.containsForbiddenWordRegex(post.getContent());
                                    ps.setBoolean(1, !containsForbiddenWords);
                                    ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                                    ps.setLong(3, post.getId());
                                }

                                @Override
                                public int getBatchSize() {
                                    return batch.size();
                                }
                            }
                    );
                    return null;
                });
            });
        }
        executorService.shutdown();
    }

    private Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост с указанным ID не существует"));
    }

    private List<PostDto> getSortedDrafts(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> getSortedPublished(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted((post1, post2) -> post2.getPublishedAt().compareTo(post1.getPublishedAt()))
                .map(postMapper::toDto)
                .toList();
    }
}
