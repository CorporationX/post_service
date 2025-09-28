package faang.school.postservice.service.post;

import faang.school.postservice.dto.event.PostNewEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.EntityDeletedException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.producer.PostNewProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.PostCacheService;
import faang.school.postservice.service.cache.UserCacheService;
import faang.school.postservice.service.user.UserService;
import faang.school.postservice.validator.OwnerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostNewProducer postNewProducer;
    private final PostRepository postRepository;
    private final PostCacheService postCacheService;
    private final UserCacheService userCacheService;
    private final OwnerValidator validator;
    private final UserService userService;

    public PostDto create(PostDto dto) {
        log.info("Create new post");
        validator.validateOwnerIds(dto.authorId(), dto.projectId());

        Post post = postMapper.toEntity(dto);
        post.setPublished(false);
        post.setDeleted(false);

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public void publish(long postId) {
        log.info("Publish post with id = {}", postId);
        Post post = findById(postId);

        if (post.isDeleted()) {
            throw new EntityDeletedException("Post {} already deleted", post.getId());
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
        postCacheService.save(post);

        UserRedis userRedis = userService.toUserRedis(post.getAuthorId(), post.getProjectId());
        userCacheService.save(userRedis);

        PostNewEvent event = postMapper.toEvent(post);
        event.setFollowees(userService.getFollowees(post.getAuthorId(), post.getProjectId()));
        postNewProducer.sendEvent(event);
        log.info("Post with id = {} was published", postId);
    }

    @Transactional
    public List<PostDto> findRecentBatchByAuthors(List<Long> authorIds, Long lastPostId, int batch) {
        List<Post> posts = postRepository.getPublishedRangeByAuthorsDesc(authorIds, lastPostId, batch);
        log.info("Got {}/{} recent posts from DB by authors", posts.size(), batch);
        return posts.stream()
                .map(postMapper::toDto).toList();
    }

    @Transactional
    public PostDto getById(long postId) {
        Post post = findById(postId);
        log.info("Found post by id = {}", postId);
        return postMapper.toDto(post);
    }

    @Transactional
    public List<PostDto> findAnyRecentBatch(int batch) {
        List<Post> posts = postRepository.findAnyRecentBatch(batch);
        if (posts.isEmpty()) {
            throw new EntityNotFoundException("Any recent posts not found");
        }
        return posts.stream()
                .map(postMapper::toDto).toList();
    }

    private Post findById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post {} not found", postId));
    }

}
