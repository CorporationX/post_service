package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
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
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public PostDto createPost(final PostDto postDto) {
        Post post = postMapper.toEntity(postDto);

        validatePostCreation(post);

        return postMapper.toDto(postRepository.save(post));
    }

    private void validatePostCreation(Post post) {
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();

        if (authorId == null && projectId == null || authorId != null && projectId != null) {
            throw new IllegalArgumentException("Post must have either author or project");
        }

        if (authorId != null) {
            UserDto user = userServiceClient.getUser(authorId);

            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
        }

        if (projectId != null) {
            ProjectDto project = projectServiceClient.getProject(projectId);

            if (project == null) {
                throw new IllegalArgumentException("Project not found");
            }
        }
    }

    public PostDto publishPost(final long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        validatePostPublishing(post);

        LocalDateTime now = LocalDateTime.now();
        post.setPublished(true);
        post.setPublishedAt(now);
        post.setUpdatedAt(now);

        return postMapper.toDto(postRepository.save(post));
    }

    private void validatePostPublishing(Post post) {
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post is already published");
        }
    }

    public PostDto updatePost(final long postId, final PostDto postDto) {
        Post newPost = postMapper.toEntity(postDto);
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.setContent(newPost.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }


    public void deletePost(final long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    public PostDto getPost(final long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return postMapper.toDto(post);
    }

    public List<PostDto> getFilteredPosts(final Long authorId, final Long projectId, final Boolean published) {
        List<Post> result = new ArrayList<>();
        boolean isPublished = published;

        if (authorId != null) {
            result = postRepository.findByAuthorIdAndPublishedAndDeletedIsFalseOrderByPublished(authorId, isPublished);
        } else if (projectId != null) {
            result = postRepository.findByProjectIdAndPublishedAndDeletedIsFalseOrderByPublished(projectId, isPublished);
        }

        return result.stream().map((postMapper::toDto)).toList();
    }
}
