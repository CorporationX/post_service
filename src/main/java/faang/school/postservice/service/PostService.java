package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.ProjectDto;
import faang.school.postservice.dto.event_broker.PostEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostEventMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostEventPublisher;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.hash.PostHashService;
import faang.school.postservice.service.hash.UserHashService;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;
    private final AsyncPostPublishService asyncPostPublishService;
    private final PostHashService postHashService;
    private final UserHashService userHashService;
    private final ModerationDictionary moderationDictionary;
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final PostEventPublisher postEventPublisher;
    private final PostViewEventPublisher postViewEventPublisher;
    private final PostEventMapper postEventMapper;

    @Value("${post.publisher.scheduler.size_batch}")
    private int sizeSublist;

    @Value("${post_moderation.batch_size}")
    int batchSize;

    public PostDto createDraftPost(PostDto postDto) {
        UserDto author = null;
        ProjectDto project = null;

        if (postDto.getAuthorId() != null) {
            author = userServiceClient.getUser(postDto.getAuthorId());
        } else if (postDto.getProjectId() != null) {
            project = projectServiceClient.getProject(postDto.getProjectId());
        }
        postValidator.validateAuthorExists(author, project);

        PostDto savePost = savePost(postDto);
        PostEvent postEvent = postEventMapper.toPostEvent(savePost);
        postHashService.savePost(postEvent);
        userHashService.saveUser(postEvent.getUserDtoAuthor());

        return savePost;
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
        Post save = postRepository.save(post);
        postEventPublisher.publish(postEventMapper.toPostEvent(post));

        return postMapper.toDto(save);
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
        postViewEventPublisher.publish(post);
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
        foundedPosts.forEach(postViewEventPublisher::publish);
        return getSortedPublished(foundedPosts);
    }

    public List<PostDto> getPublishedPostsByProject(long projectId) {
        List<Post> foundedPosts = postRepository.findByProjectIdWithLikes(projectId);
        return getSortedPublished(foundedPosts);
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

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new faang.school.postservice.exception.DataValidationException("Post has not found"));
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

        for (int i = 0; i < posts.size(); i += batchSize) {
            final int startIndex = i;
            executorService.submit(() -> {
                transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                transactionTemplate.execute(status -> {
                    final List<Post> batch = posts.subList(startIndex, Math.min(startIndex + batchSize, posts.size()));
                    jdbcTemplate.batchUpdate("UPDATE post SET verified = ?, verified_date = ? WHERE id = ?",
                            new BatchPreparedStatementSetter() {
                                @Override
                                public void setValues(PreparedStatement ps, int j) throws SQLException {
                                    Post post = batch.get(j);
                                    boolean containsForbiddenWords = moderationDictionary.containsForbiddenWordRegex(post.getContent());
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
}