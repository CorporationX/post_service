package faang.school.postservice.service;

import faang.school.postservice.dto.posts.PostCreatingRequest;
import faang.school.postservice.dto.posts.PostResultResponse;
import faang.school.postservice.dto.posts.PostUpdatingDto;
import faang.school.postservice.exceptions.PostWasNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.PostAuthorCacheService;
import faang.school.postservice.service.moderation.ModerationDictionary;
import faang.school.postservice.utils.PostUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private static final int MAX_POST_COUNT_PUBLISH_ON_THREAD = 1000;
    private static final int MODERATION_PER_PAGE = 100;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostUtil postUtil;
    private final RewriterService rewriterService;
    private final ExecutorService scheduledPublishPostThreadPool;
    private final ObjectFactory<ModerationDictionary> moderationDictionaryObjectFactory;
    private final PostAuthorCacheService postAuthorCacheService;

    @Transactional
    public PostResultResponse createPost(PostCreatingRequest postCreatingDto) {
        Post post = Post.builder()
                .content(postCreatingDto.content())
                .published(false)
                .deleted(false)
                .build();

        log.info("Creating the post with id : {}", post.getId());

        log.info("Validating the post creator with id : {}", post.getId());
        int result = postUtil.validateCreator(postCreatingDto.authorId(), postCreatingDto.projectId());
        switch (result) {
            case 0:
                post.setAuthorId(postCreatingDto.authorId());
                break;
            case 1:
                post.setProjectId(postCreatingDto.projectId());
                break;
            default:
        }
        log.info("Success validation for post : {}", post.getId());

        post = postRepository.save(post);
        log.info("Saved post with id : {}", post.getId());

        return postMapper.toDto(post);
    }

    @Transactional
    public PostResultResponse publishPost(Long postId) {
        log.info("Publishing post with id : {}", postId);
        Post post = findPostById(postId);
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post already published!");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        log.info("Successfully published post with id : {}", postId);

        postAuthorCacheService.cachePostAuthor(post.getAuthorId());
        log.info("Successfully published and cached author for post id: {}", postId);

        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public void publishScheduledPost() {
        log.info("publishing scheduled posts");

        List<Post> posts = postRepository.findReadyToPublish();
        List<List<Post>> groups = divideListIntoGroups(posts, MAX_POST_COUNT_PUBLISH_ON_THREAD);

        groups.forEach(group -> {
            scheduledPublishPostThreadPool.submit(() -> {
                group.forEach(item -> {
                    publishPost(item.getId());
                });
            });
        });
    }

    @Transactional
    public PostResultResponse updatePost(PostUpdatingDto postUpdatingDto) {
        Long postId = postUpdatingDto.postId();
        String updatingContent = postUpdatingDto.updatingContent();
        log.info("Updating post with id : {}", postId);
        Post post = findPostById(postId);
        if (post.isDeleted() || !post.isPublished()) {
            throw new IllegalArgumentException("Post deleted or not published yet!");
        }
        post.setContent(updatingContent);
        log.info("Successfully updated post with id : {}", postId);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostResultResponse softDelete(Long postId) {
        log.info("Soft deleting post with id : {}", postId);
        Post post = findPostById(postId);
        if (post.isDeleted()) {
            throw new IllegalArgumentException("Post already marked as deleted!");
        }
        post.setDeleted(true);
        log.info("Successfully soft deleted post with id : {}", postId);
        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResultResponse> getNoPublishedPostsByAuthor(Long authorId) {
        return getPostsByFilter(authorId, postRepository::findByAuthorId, post -> !post.isPublished());
    }

    @Transactional(readOnly = true)
    public List<PostResultResponse> getNoPublishedPostsByProject(Long projectId) {
        return getPostsByFilter(projectId, postRepository::findByProjectId, post -> !post.isPublished());
    }

    @Transactional(readOnly = true)
    public List<PostResultResponse> getPublishedPostsByAuthor(Long authorId) {
        return getPostsByFilter(authorId, postRepository::findByAuthorId, Post::isPublished);
    }

    @Transactional(readOnly = true)
    public List<PostResultResponse> getPublishedPostsByProject(Long projectId) {
        return getPostsByFilter(projectId, postRepository::findByProjectId, Post::isPublished);
    }

    public List<PostResultResponse> getPostsByFilter(Long id,
                                                     Function<Long, List<Post>> fetcher,
                                                     Predicate<Post> filter) {
        return fetcher.apply(id)
                .stream()
                .filter(filter)
                .map(postMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Post findPostById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostWasNotFoundException("No posts was found!"));
    }

    private List<List<Post>> divideListIntoGroups(List<Post> items, int groupSize) {
        List<List<Post>> groups = new ArrayList<>();
        List<Post> currentGroup = new ArrayList<>();
        for (Post item : items) {
            currentGroup.add(item);
            if (currentGroup.size() >= groupSize) {
                groups.add(currentGroup);
                currentGroup = new ArrayList<>();
            }
        }
        if (!currentGroup.isEmpty()) {
            groups.add(currentGroup);
        }
        return groups;
    }

    public boolean existsById(long id) {
        return postRepository.existsById(id);
    }

    public void postCorrections() {
        List<Post> posts = postRepository.findReadyToPublish();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Post post : posts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> processRewritePost(post));
            futures.add(future);
        }

        CompletableFuture.runAsync(() -> {
            futures.forEach(CompletableFuture::join);
            log.info("Successfully rewrited posts");
        });
    }

    public void processRewritePost(Post post) {
        String newText = rewriterService.rewriteText(post.getContent());
        post.setContent(newText);
        postRepository.save(post);
        log.info("Rewriting post with id : {}", post.getId());
    }

    public void moderationPosts() {
        log.info("Moderating posts");
        ModerationDictionary moderationDictionary = moderationDictionaryObjectFactory.getObject();
        Set<String> moderationSet = moderationDictionary.getModerationSet();
        int page = 0;

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Page<Post> postPage;
        do {
            Pageable pageable = PageRequest.of(page, MODERATION_PER_PAGE);
            postPage = postRepository.findUnverifiedPosts(pageable);
            List<Post> content = postPage.getContent();

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> moderateBatch(content, moderationSet));
            futures.add(future);

            page++;
        } while (postPage.hasNext());

        futures.forEach(CompletableFuture::join);
        log.info("Successfully moderated posts");
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostWasNotFoundException("No posts was found!"));
    }

    private void moderateBatch(List<Post> posts, Set<String> moderationSet) {
        posts.forEach(post -> moderatePost(post, moderationSet));
    }

    private void moderatePost(Post post, Set<String> moderationSet) {
        String content = post.getContent().toLowerCase();

        boolean containsObsceneWord = moderationSet.stream()
                .anyMatch(moderationWord -> content.contains(moderationWord));

        post.setVerified(!containsObsceneWord);
        post.setVerifiedAt(LocalDateTime.now());
        postRepository.save(post);
        log.info("moderated post with id: {}. Verified: {}", post.getId(), post.isVerified());
    }
}