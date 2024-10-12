package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostValidator validator;
    private final PostMapper postMapper;
    private final PostPublisher postPublisher;

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

    private boolean existsCreator(PostDto postDto) {
        if (postDto.getAuthorId() == null) {
            return projectServiceClient.existsProjectById(postDto.getProjectId());
        } else {
            return userServiceClient.existsUserById(postDto.getAuthorId());
        }
    }

    @Override
    public void publishPost(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no post with ID " + id));

        if (!post.isPublished()) {
            post.setPublishedAt(LocalDateTime.now());
            post.setPublished(true);
            postRepository.save(post);

            postPublisher.publish(new PostEvent(post.getAuthorId(), post.getId()));
            log.info("post event id = {} published to post_channel", post.getId());
        }
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
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no post with ID " + id));
        return postMapper.toDto(post);
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

    @Override
    public List<Long> getAuthorsWithMoreFiveUnverifiedPosts() {
        return postRepository.findAuthorsWithMoreThanFiveUnverifiedPosts();
    }
}
