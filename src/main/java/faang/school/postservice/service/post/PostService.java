package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.TaskExecutorConfig;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.exception.DataSavingException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.KafkaPostProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostServiceValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostServiceValidator<PostDto> validator;
    private final UserServiceClient userServiceClient;
    private final KafkaPostProducer kafkaPostProducer;
    private TaskExecutorConfig taskExecutor;

    @Transactional
    public PostDto createPost(final PostDto postDto) {
        try {
            //validator.validate(postDto);
            Post post = postMapper.toEntity(postDto);
            return postMapper.toDto(postRepository.save(post));
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating draft post: {}", postDto, e);
            throw new DataSavingException("Error saving draft post. Check the data is correct.", e);
        }
    }

    @Transactional
    public PostDto publishPost(final long postId) {
        Post post = getPostByIdOrFail(postId);
        //validatePostPublishing(post);
        LocalDateTime now = LocalDateTime.now();
        post.setPublished(true);
        post.setPublishedAt(now);
        post.setUpdatedAt(now);
        Post saved = postRepository.save(post);

        Long authorId = saved.getAuthorId();
        List<Long> subscribersIds = userServiceClient.getFollowers(authorId);
        kafkaPostProducer.publish(authorId, subscribersIds);
        return postMapper.toDto(saved);
    }

    @Async(value = "taskExecutor")
    public void publishInBatches(Long authorId, List<Long> subscribersIds) {
        for (int i = 0; i < subscribersIds.size(); i += batchSize) {
            List<Long> batch = subscribersIds.subList(i, Math.min(i + batchSize, subscribersIds.size()));
            executor.submit(() -> kafkaPostProducer.publish(authorId, batch));
        }
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
        return postRepository.findById(postId).orElseThrow(() -> new DataNotFoundException("Post not found"));
    }
}
