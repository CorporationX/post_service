package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.kafka.producer.KafkaEventProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
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
    private final KafkaEventProducer kafkaEventProducer;
    private final UserServiceClient userServiceClient;
    private final PostCacheService postCacheService;

    public PostDto createPost(final PostDto postDto) {
        validator.validate(postDto);
        Post post = postMapper.toEntity(postDto);
        return postMapper.toDto(postRepository.save(post));
    }


    public PostDto publishPost(final long postId) throws JsonProcessingException {
        Post post = getPostByIdOrFail(postId);

        validatePostPublishing(post);

        LocalDateTime now = LocalDateTime.now();
        post.setPublished(true);
        post.setPublishedAt(now);
        post.setUpdatedAt(now);


        PostDto postDto = postMapper.toDto(postRepository.save(post));
        postCacheService.savePostEvent(postDto);

        return postDto;
    }

    private void validatePostPublishing(Post post) {
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post is already published");
        }
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

        return postMapper.toDto(post);
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

    private Post getPostByIdOrFail(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }
}
