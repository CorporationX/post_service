package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CreatePostDto;
import faang.school.postservice.exception.AuthorNotFoundException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.RedisUserBanTopicPublisher;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostRedisRepository postRedisRepository;

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    private final RedisUserBanTopicPublisher redisUserBanTopicPublisher;

    @Value("${app.moderation.post-count-threshold}")
    private int postCountThreshold;

    @Value("${news-feed.posts.redis-ttl:86400}")
    private long postRedisTtl;

    public CreatePostDto create(CreatePostDto createPostDto) {
        validateContent(createPostDto);
        validateAuthor(createPostDto.authorId(), createPostDto.projectId());
        var post = postMapper.toEntity(createPostDto);

        postRepository.save(post);
        log.info("Post created: {}", post);

        var postRedis = postMapper.toPostRedis(post);
        postRedis.setTimeToLive(postRedisTtl);
        postRedisRepository.save(postRedis);

        return postMapper.toCreatedPostDto(post);
    }

    @Override
    public Post findPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException(String.format("Post with id %s not found", postId)));
    }

    @Override
    public void banUsersIfRequired() {
        List<Long> authorIdsToBan;
        try {
            authorIdsToBan = postRepository.findAuthorIdsByUnverifiedPostsThreshold(postCountThreshold);
        } catch (DataAccessException ex) {
            throw new faang.school.postservice.exception.DataAccessException(ex.getMessage());
        }

        authorIdsToBan.forEach(redisUserBanTopicPublisher::publish);
    }

    private void validateContent(CreatePostDto createPostDto) {
        if (createPostDto.content() == null || createPostDto.content().isBlank()) {
            throw new NullPointerException("Content is null or empty");
        }
    }

    private void validateAuthor(Long authorId, Long projectId) {
        boolean isProject = projectId != null;
        boolean isUser = authorId != null;

        if (isProject && isUser) {
            throw new IllegalArgumentException("Only one author must be specified: either the user or the project.");
        }

        if (isProject && !existsProject(projectId)) {
            throw new AuthorNotFoundException("Project with ID " + projectId + " does not exist.");
        }

        if (isUser && !existsUser(authorId)) {
            throw new AuthorNotFoundException("Author with ID " + authorId + " does not exist.");
        }
    }

    private boolean existsUser(Long authorId) {
        try {
            userServiceClient.getUser(authorId);
            return true;
        } catch (FeignException e) {
            return false;
        }
    }

    private boolean existsProject(Long projectId) {
        try {
            projectServiceClient.getProject(projectId);
            return true;
        } catch (FeignException e) {
            return false;
        }
    }
}
