package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.filter.FilterDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.events.BanUserEvent;
import faang.school.postservice.events.PostViewEvent;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.publisher.RedisBanMessagePublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.sort.PostField;
import faang.school.postservice.sort.SortBy;
import faang.school.postservice.validator.PostServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final List<PostFilter> postFilters;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final List<SortBy> sort;
    private final CommentService commentService;
    private final RedisBanMessagePublisher redisBanMessagePublisher;
    private final CacheService cacheService;
    private final PostViewEventPublisher postViewEventPublisher;

    public PostDto createPost(PostDto postDto) {
        log.info("validate post dto argument");
        PostServiceValidator.checkDtoValidAuthorOrProjectId(postDto);
        PostServiceValidator.checkThatUserOrProjectIsExist(postDto, userServiceClient, projectServiceClient);

        log.info("mapping postDto to entity");
        Post post = postMapper.toEntity(postDto);
        post.setCreatedAt(LocalDateTime.now());
        post.setPublished(false);
        post.setDeleted(false);

        log.info("saving new post in db");
        postRepository.save(post);

        log.info("mapping entity to postDto");
        return postMapper.toDto(post);
    }

    public PostDto publishPost(long id) {
        Post post = getPost(id);
        log.info("validate post for existence");
        PostServiceValidator.checkPostWasPosted(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(post.getPublishedAt());

        log.info("update at db post " + post.getId());
        savePost(post);

        log.info("calling cache service to add new post to redis");
        cacheService.publishPostAndAuthorAtRedis(post);

        log.info("mapping entity to dto");
        return postMapper.toDto(post);
    }

    public PostDto updatePost(long id, PostDto postDto) {
        Post post = getPost(id);

        log.info("mapping update to post " + post.getId());
        postMapper.update(postDto, post);

        log.info("update at db post " + post.getId());
        savePost(post);

        cacheService.updatePostAtRedis(post);

        log.info("mapping entity to dto");
        return postMapper.toDto(post);
    }

    public PostDto getPostDto(long id) {
        log.info("getting post from db");
        Post post = getPost(id);

        postViewEventPublisher.sendMessage(new PostViewEvent(post.getId()));

        log.info("mapping entity to dto");
        return postMapper.toDto(getPost(id));
    }

    public PostDto deletePost(long id) {
        Post post = getPost(id);

        post.setDeleted(true);
        post.setPublished(false);
        post.setUpdatedAt(LocalDateTime.now());

        log.info("update at db post " + post.getId());
        savePost(post);

        cacheService.deletePostFromRedis(id);

        log.info("mapping entity to dto");
        return postMapper.toDto(post);
    }

    public List<PostDto> getPostsById(Long authorId, FilterDto filterDto) {
        List<Post> posts = getPostWithLikes(authorId, filterDto.getAuthor());

        if (posts == null) {
            log.warn("post was null, return empty array list");
            return new ArrayList<>();
        }

        log.info("apply filters to list of posts");
        postFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(posts, filterDto));

        List<PostDto> result = sort(posts, filterDto.getPostField());
        result.forEach(postDto -> postViewEventPublisher.sendMessage(new PostViewEvent(postDto.getId())));

        return result;
    }

    public Post getPost(long id) {
        log.warn("trying to get entity from db by id");
        return postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Post %d not found", id)));
    }

    public boolean existsPostById(Long id) {
        if (id == null) return false;

        log.info("checking post for existence");
        return postRepository.existsById(id);
    }

    public void findUserToBan() {
        log.info("grouping comments by authorId and send ban message");
        commentService.getAllNotVerifiedComments().stream()
                .collect(Collectors.groupingBy(
                        Comment::getAuthorId,
                        Collectors.counting()))
                .forEach((key, value) -> {
                    if (value > 0) {
                        BanUserEvent banUserEvent = BanUserEvent.builder()
                                .userId(key)
                                .commentCount(value)
                                .build();
                        redisBanMessagePublisher.publish(banUserEvent);
                    }
                });
        log.info("updating all unVerified comments");
        commentService.markAsRemovedUnVerifiedComments();
    }

    private List<PostDto> sort(List<Post> posts, PostField postField) {
        log.info("finding comparator by postField");
        Comparator<Post> comparator = sort.stream()
                .filter(sortBy -> sortBy.getPostField() == postField)
                .findFirst()
                .map(SortBy::getComparator)
                .orElseThrow(() -> new IllegalStateException("Такого компаратора не существует"));
        log.info("sort by founded comparator and mapping to dto");
        return posts.stream()
                .sorted(comparator)
                .map(postMapper::toDto)
                .toList();
    }

    private List<Post> getPostWithLikes(long id, boolean author) {
        if (author) {
            log.warn("trying to get entity from db by id");
            return postRepository.findByAuthorIdWithLikes(id);
        } else {
            log.warn("trying to get entity from db by id");
            return postRepository.findByProjectIdWithLikes(id);
        }
    }

    private void savePost(Post post) {
        log.warn("saving entity in db");
        postRepository.save(post);
    }

    public Optional<Post> findPostById(Long id) {
        log.warn("trying to get entity from db by id");
        return postRepository.findById(id);
    }
}
