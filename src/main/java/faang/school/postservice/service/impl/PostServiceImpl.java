package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostValidator validator;
    private final PostMapper postMapper;


    @Override
    @Transactional
    public void createDraftPost(PostDto postDto) {
        validator.validatePost(postDto);
        validator.validateCreator(existsCreator(postDto));

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
    @Transactional
    public void publishPost(long id) {
        Post optionalPost = postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + id));
        Post post = postMapper.toEntity(validator.validatePostWithReturnDto(postMapper.toDto(optionalPost)));

        if (!post.isPublished()) {
            post.setPublishedAt(LocalDateTime.now());
            post.setPublished(true);
            postRepository.save(post);
        }
    }

    @Override
    @Transactional
    public void updateContentPost(String newContent, long id) {
        postRepository.updateContentByPostId(id, newContent);
    }

    @Override
    @Transactional
    public void softDeletePost(long id) {
        postRepository.softDeletePostById(id);
    }

    @Override
    public PostDto getPost(long id) {
        Post optionalPost = postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + id));
        Post post = postMapper.toEntity(validator.validatePostWithReturnDto(postMapper.toDto(optionalPost)));
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
}
