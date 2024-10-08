package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.kafka.EventsGenerator;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.service.AuthorCacheService;
import faang.school.postservice.redis.service.PostCacheService;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostServiceValidator<PostDto> validator;

    private final PostCacheService postCacheService;
    private final EventsGenerator eventsGenerator;
    private final AuthorCacheService authorCacheService;

    public PostDto createPost(final PostDto postDto) {
        validator.validate(postDto);

        Post post = postMapper.toEntity(postDto);

        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto publishPost(final long postId) {
        Post post = getPostByIdOrFail(postId);

        validatePostPublishing(post);

        LocalDateTime now = LocalDateTime.now();
        post.setPublished(true);
        post.setPublishedAt(now);
        post.setUpdatedAt(now);

        var savedPost = postRepository.save(post);
        var postDto = postMapper.toDto(savedPost);

        authorCacheService.saveAuthorCache(postDto.getAuthorId());
        postCacheService.savePostCache(postDto);
        eventsGenerator.generateAndSendPostFollowersEvent(postDto);

        return postDto;
    }


    public PostDto updatePost(final long postId, final PostDto postDto) {
        Post newPost = postMapper.toEntity(postDto);
        Post post = getPostByIdOrFail(postId);

        post.setContent(newPost.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }


    public void deletePost(final long postId) {
        Post post = getPostByIdOrFail(postId);

        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    public PostDto getPost(final long postId) {
        Post post = getPostByIdOrFail(postId);
        var postDto = postMapper.toDto(post);

        eventsGenerator.generateAndSendPostViewEvent(postDto);
        return postDto;
    }

    public List<PostDto> getPostsByIds(List<Long> postIds) {
        return postRepository.findAllById(postIds).stream()
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> getFilteredPosts(final Long authorId, final Long projectId, final Boolean isPostPublished) {
        List<Post> result = new ArrayList<>();
        boolean isPublished = isPostPublished;

        if (authorId != null) {
            result = postRepository.findByAuthorIdAndPublishedAndDeletedIsFalseOrderByPublished(authorId, isPublished);
        } else if (projectId != null) {
            result = postRepository.findByProjectIdAndPublishedAndDeletedIsFalseOrderByPublished(projectId, isPublished);
        }

        return result.stream()
                .map((postMapper::toDto))
                .toList();
    }

    private void validatePostPublishing(Post post) {
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post is already published");
        }
    }

    private Post getPostByIdOrFail(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }
}