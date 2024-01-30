package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostValidator postValidator;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;

    public PostDto createDraftPost(PostDto postDto) {
        UserDto author = null;
        ProjectDto project = null;

        if (postDto.getAuthorId() != null) {
            author = userServiceClient.getUser(postDto.getAuthorId());
        } else if (postDto.getProjectId() != null) {
            project = projectServiceClient.getProject(postDto.getProjectId());
        } else {
            throw new IllegalArgumentException("Необходимо указать автора или проект");
        }
        postValidator.validateAuthorExists(author, project);

        return savePost(postDto);
    }

    public PostDto savePost(PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto publishPost(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост с указанным ID не существует"));
        postValidator.validateIsNotPublished(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto updatePost(PostDto postDto) {
        Post post = findById(postDto.getId());
        postValidator.validateCreatorNotChanged(postDto, post);

        return savePost(postDto);
    }

    private Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост с указанным ID не существует"));
    }

    public boolean deletePost(long id) {
        Post post = findById(id);
        if (post.isDeleted()) {
            throw new DataValidationException("Поста уже удален");
        } else {
            post.setDeleted(true);
            postRepository.save(post);
            return true;
        }
    }

    public PostDto getPost(long id) {
        Post post = findById(id);
        return postMapper.toDto(post);
    }

    public List<PostDto> getDraftsByUser(long userId) {
        List<Post> foundedPosts = postRepository.findByAuthorId(userId);
        return getSortedDrafts(foundedPosts);
    }

    public List<PostDto> getDraftsByProject(long projectId) {
        List<Post> foundedPosts = postRepository.findByProjectId(projectId);
        return getSortedDrafts(foundedPosts);
    }

    private List<PostDto> getSortedDrafts(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted())
                .filter(post -> !post.isPublished())
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> getSortedPublished(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted())
                .filter(Post::isPublished)
                .sorted((post1, post2) -> post2.getPublishedAt().compareTo(post1.getPublishedAt()))
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> getPublishedPostsByUser(long userId) {
        List<Post> foundedPosts = postRepository.findByAuthorIdWithLikes(userId);
        return getSortedPublished(foundedPosts);
    }

    public List<PostDto> getPublishedPostsByProject(long projectId) {
        List<Post> foundedPosts = postRepository.findByProjectIdWithLikes(projectId);
        return getSortedPublished(foundedPosts);
    }
}
