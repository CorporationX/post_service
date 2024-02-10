package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final ModerationDictionary moderationDictionary;
    private final JdbcTemplate jdbcTemplate;
    private final  TransactionTemplate transactionTemplate;

    @Value("${post_moderation.batch_size}")
    int batchSize;

    @Transactional
    public PostDto createDraftPost(PostDto postDto) {
        validateIdPostDto(postDto);
        validateAuthorExist(postDto);

        Post post = postMapper.toEntity(postDto);

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto publishPost(Long id) {
        Post post = validatePostExist(id);

        if (post.isPublished() || post.isDeleted()) {
            throw new DataValidationException("Post is already published or deleted");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        validateIdPostDto(postDto);
        validateAuthorExist(postDto);
        Post post = validatePostExist(postDto.getId());

        post.setContent(postDto.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        return postMapper.toDto(post);
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
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            log.error("Ошибка модерации постов", e);
            Thread.currentThread().interrupt();
        }
    }

    private void validateIdPostDto(PostDto postDto) {
        if ((postDto.getAuthorId() == null && postDto.getProjectId() == null) ||
                (postDto.getAuthorId() != null && postDto.getProjectId() != null)) {
            throw new DataValidationException("Enter one thing: authorId or projectId");
        }
    }

    private void validateAuthorExist(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            try {
                userServiceClient.getUser(postDto.getAuthorId());
            } catch (FeignException e) {
                throw new EntityNotFoundException("User with the specified authorId does not exist");
            }
        } else if (postDto.getProjectId() != null) {
            try {
                projectServiceClient.getProject(postDto.getProjectId());
            } catch (FeignException e) {
                throw new EntityNotFoundException("Project with the specified projectId does not exist");
            }
        }
    }

    private Post validatePostExist(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with the specified id does not exist"));
    }
}
