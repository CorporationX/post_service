package faang.school.postservice.service;

import faang.school.postservice.exception.DataLikeValidation;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;






import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostValidator postValidator;
    private final PostMapper postMapper;

    @Transactional
    public PostDto createDraftPost(PostDto postDto) {
        UserDto author = userServiceClient.getUser(postDto.getAuthorId());
        ProjectDto project = projectServiceClient.getProject(postDto.getProjectId());
        postValidator.validateAuthorExist(author, project);

        Post saved = postRepository.save(postMapper.toEntity(postDto));
        return postMapper.toDto(saved);
    }

    @Transactional
    public PostDto publishDraftPost(Long id) {
        Post post = getById(id);
        postValidator.validateIsNotPublished(post);
        post.setPublished(true);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(PostDto postDto, Long id) {
        Post post = getById(id);
        postValidator.validateChangeAuthor(post, postDto);

        post.setContent(postDto.getContent());
        return postMapper.toDto(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = getById(id);
        post.setDeleted(true);
    }

    @Transactional
    public PostDto getPost(Long id) {
        Post post = getById(id);
        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getUserDrafts(Long userId) {
        List<Post> drafts = postRepository.findByAuthorId(userId);
        postValidator.validatePostsExists(drafts);
        return getNonDeletedSortedPostsDto(drafts, post -> !post.isPublished());
    }

    @Transactional(readOnly = true)
    public List<PostDto> getProjectDrafts(Long projectId) {
        List<Post> drafts = postRepository.findByProjectId(projectId);
        postValidator.validatePostsExists(drafts);
        return getNonDeletedSortedPostsDto(drafts, post -> !post.isPublished());
    }

    @Transactional(readOnly = true)
    public List<PostDto> getUserPosts(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        postValidator.validatePostsExists(posts);
        return getNonDeletedSortedPostsDto(posts, Post::isPublished);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getProjectPosts(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        postValidator.validatePostsExists(posts);
        return getNonDeletedSortedPostsDto(posts, Post::isPublished);
    }

    @Transactional
    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new DataLikeValidation("Поста с id " + postId + " нет в базе данных."));
    }

    private List<PostDto> getNonDeletedSortedPostsDto(List<Post> posts, Predicate<Post> predicate) {
        return posts.stream()
                .filter(predicate)
                .filter(post -> !post.isDeleted())
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();
    }

    private Post getById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Поста с указанным id " + id + " не существует"));
    }
}
