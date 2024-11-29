package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserFilterDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.AsyncPostPublishService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.cache.MultiSaveCacheService;
import faang.school.postservice.service.cache.SingleCacheService;
import faang.school.postservice.util.CollectionUtils;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.aspectj.weaver.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    @Value("${post.publisher.scheduler.size_batch}")
    private int sizeBatch;

    private final PostRepository postRepository;
    private final SingleCacheService<Long, PostDto> singleCacheService;
    private final MultiSaveCacheService<PostDto> multiSaveCacheService;
    private final SingleCacheService<Long, UserDto> cacheUserRepository;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostValidator validator;
    private final PostMapper postMapper;
    private final AsyncPostPublishService asyncPostPublishService;
    private final EventPublisher<FeedEventProto.FeedEvent> postForFeedPublisher;
    private final EventPublisher<PostDto> viewPostPublisher;
    private final TransactionTemplate transactionTemplate;
    private final ExecutorService newsFeedThreadPoolExecutor;
    private final CollectionUtils collectionUtils;

    @Override
    public void createDraftPost(PostDto postDto) {
        validator.validatePost(postDto);
        if (existsCreator(postDto)) {
            throw new DataValidationException("There is no project/user");
        }

        postDto.setDeleted(false);
        postDto.setPublished(false);

        Post newPost = postMapper.toEntity(postDto);
        postRepository.save(newPost);
    }

    @Override
    public void publishPost(long id) {
        Post postOrNull = transactionTemplate.execute(transactionStatus -> publishPostAndGet(id));
        Optional.ofNullable(postOrNull)
                .ifPresent((post) -> newsFeedThreadPoolExecutor.execute(() -> {
                    saveToCache(post);
                    publishForFeed(post);
                }));
    }

    @Override
    public void updateContentPost(String newContent, long id) {
        postRepository.updateContentByPostId(id, newContent);
    }

    @Override
    public void softDeletePost(long id) {
        postRepository.softDeletePostById(id);
    }

    @Override
    public PostDto getPost(long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("There is no post with ID " + id));
    }

    @Override
    public List<PostDto> getPosts(List<Long> postIds) {
        List<PostDto> posts = new ArrayList<>(postIds.size());
        List<Long> missingPostIds = new ArrayList<>();

        for (Long postId : postIds) {
            PostDto postDto = singleCacheService.get(postId);
            if (postDto == null) {
                missingPostIds.add(postId);
            }
            posts.add(postDto);
        }

        List<Post> missingPosts = postRepository.findAllById(missingPostIds);
        List<PostDto> missingPostDtos = postMapper.toDto(missingPosts);
        multiSaveCacheService.saveAll(missingPostDtos);
        collectionUtils.replaceNullsWith(posts, missingPostDtos);

        return posts;
    }

    @Override
    public List<PostDto> getDraftPostsByUserId(long id) {
        List<Post> posts = postRepository.findByAuthorIdAndUnpublished(id);
        return postMapper.toDto(posts);
    }

    @Override
    public List<PostDto> getDraftPostsByProjectId(long id) {
        List<Post> posts = postRepository.findByProjectIdAndUnpublished(id);
        return postMapper.toDto(posts);
    }

    @Override
    public List<PostDto> getPublishedPostsByUserId(long id) {
        List<Post> posts = postRepository.findByAuthorIdAndPublished(id);
        return postMapper.toDto(posts);
    }

    @Override
    public List<PostDto> getPublishedPostsByProjectId(long id) {
        List<Post> posts = postRepository.findByProjectIdAndPublished(id);
        return postMapper.toDto(posts);
    }

    @Transactional
    @Override
    public void publishScheduledPosts() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Post> postsToPublish = postRepository.findReadyToPublish();

        if (!postsToPublish.isEmpty()) {
            log.info("Size of posts list publish is {}", postsToPublish.size());
            List<List<Post>> subLists = ListUtils.partition(postsToPublish, sizeBatch);
            subLists.forEach(asyncPostPublishService::publishPost);
            log.info("Finished publish all posts at {}", currentDateTime);
        } else {
            log.info("Unpublished posts at {} not found", currentDateTime);
        }
    }

    @Override
    public List<Long> getAuthorsWithMoreFiveUnverifiedPosts() {
        return postRepository.findAuthorsWithMoreThanFiveUnverifiedPosts();
    }

    private Post publishPostAndGet(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no post with ID " + id));

        if (!post.isPublished()) {
            post.setPublishedAt(LocalDateTime.now());
            post.setPublished(true);
            postRepository.save(post);
        }

        return post;
    }

    private void saveToCache(Post post) {
        UserDto user = userServiceClient.getUser(post.getAuthorId());
        singleCacheService.save(post.getId(), postMapper.toDto(post));
        cacheUserRepository.save(user.getId(), user);
    }

    private void publishForFeed(Post post) {
        Iterable<Long> followers = userServiceClient.getFollowers(post.getAuthorId(), new UserFilterDto())
                .stream()
                .map(UserDto::getId)
                .toList();
        FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.newBuilder()
                .setPostId(post.getId())
                .addAllFollowerIds(followers)
                .build();

        postForFeedPublisher.publish(feedEvent);
        viewPostPublisher.publish(postMapper.toDto(post));
    }

    private boolean existsCreator(PostDto postDto) {
        if (postDto.getAuthorId() == null) {
            return projectServiceClient.existsProjectById(postDto.getProjectId());
        } else {
            return userServiceClient.existsUserById(postDto.getAuthorId());
        }
    }
}
