package faang.school.postservice.service;

import faang.school.postservice.service.async.AsyncPostService;
import org.springframework.beans.factory.annotation.Value;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostEventPublisher;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final ProjectServiceClient projectServiceClient;
    private final PostEventPublisher postEventPublisher;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final AsyncPostService asyncPostService;
    @Value("${post.publisher.scheduler.batch-size}")
    private int batchSize;

    @Transactional
    public PostDto createDraft(PostDto postDto) {
        validateAuthor(postDto);
        Post post = postMapper.toEntity(postDto);
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publish(long id) {
        Post post = searchPostById(id);
        if (post.isPublished()) {
            throw new DataValidationException("The post has already been published");
        }
        postEventPublisher.publish(new PostEvent(post.getAuthorId(), post.getId()));
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);

        postEventPublisher.publish(new PostEvent(post.getAuthorId(), post.getId()));

        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto update(PostDto postDto) {
        Post post = searchPostById(postDto.getId());
        post.setContent(postDto.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto deletePost(long id) {
        Post post = searchPostById(id);
        post.setPublished(false);
        post.setDeleted(true);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto getPostById(long id) {
        return postMapper.toDto(searchPostById(id));
    }

    @Transactional
    public List<PostDto> getDraftsByAuthorId(long id) {
        List<Post> posts = postRepository.findByAuthorId(id);
        return filterPosts(posts, false);
    }

    @Transactional
    public List<PostDto> getDraftsByProjectId(long id) {
        List<Post> posts = postRepository.findByProjectId(id);
        return filterPosts(posts, false);
    }

    @Transactional
    public List<PostDto> getPublishedPostsByAuthorId(long id) {
        List<Post> posts = postRepository.findByAuthorId(id);
        return filterPosts(posts, true);
    }

    @Transactional
    public List<PostDto> getPublishedPostsByProjectId(long id) {
        List<Post> posts = postRepository.findByProjectId(id);
        return filterPosts(posts, true);
    }

    public void publishScheduledPosts() {
        List<Post> posts = postRepository.findReadyToPublish();

        for (int i = 0; i < posts.size(); i += batchSize) {
            int finalI = i;
            if (i + batchSize < posts.size()) {
                asyncPostService.publishThousandPosts(posts, finalI, finalI + batchSize);
            } else {
                asyncPostService.publishThousandPosts(posts, finalI, posts.size());
            }
        }
    }

    private void validateAuthor(PostDto postDto) {
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("The author of the post is not specified");
        }
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("A post cannot have two authors");
        }
        if (postDto.getAuthorId() != null && !userServiceClient.existById(postDto.getAuthorId())) {
            throw new DataValidationException("There is no author with this id " + postDto.getAuthorId());
        }

    }

    private Post searchPostById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Post with id " + id + " not found."));
    }

    private List<PostDto> filterPosts(List<Post> posts, boolean isPublished) {
        return posts.stream()
                .filter(post -> post.isPublished() == isPublished)
                .filter(post -> !post.isDeleted())
                .sorted((post1, post2) -> {
                    LocalDateTime date1 = isPublished ? post1.getPublishedAt() : post1.getCreatedAt();
                    LocalDateTime date2 = isPublished ? post2.getPublishedAt() : post2.getCreatedAt();
                    if (date1 == null || date2 == null) {
                        throw new DataValidationException("Invalid date");
                    }
                    return date2.compareTo(date1);
                })
                .map(postMapper::toDto)
                .toList();
    }
}