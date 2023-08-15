package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;


    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with id " + postId + "not found"));
    }

    @Transactional
    public PostDto createPost(PostDto post) {
        ProjectDto project = null;
        UserDto user = null;

        if (post.getProjectId() != null) {
            project = projectServiceClient.getProject(post.getProjectId());
        } else if (post.getAuthorId() != null) {
            user = userServiceClient.getUser(post.getAuthorId());
        }

        postValidator.validatePostCreator(post, project, user);
        postValidator.validatePostContent(post);

        Post postEntity = postMapper.toPost(post);

        return postMapper.toDto(postRepository.save(postEntity));
    }

    @Transactional
    public PostDto publishPost(Long postId) {
        Post post = getPostById(postId);

        postValidator.validatePublishPost(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto updatePost(PostDto postUpdateDto) {
        Post post = getPostById(postUpdateDto.getId());
        postValidator.validationOfPostUpdate(postUpdateDto, post);

        Post updatedPost = postMapper.toPost(postUpdateDto);

        return postMapper.toDto(postRepository.save(updatedPost));
    }

    @Transactional(readOnly = true)
    public PostDto getPost(Long postId) {
        return postMapper.toDto(getPostById(postId));
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNotDeletedDraftsByAuthorId(Long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        postValidator.validateAuthor(user);
        List<Post> draftsByAuthorId = postRepository.findDraftsByAuthorId(user.getId());
        return getSortedDrafts(draftsByAuthorId);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNotDeletedDraftsByProjectId(Long projectId) {
        ProjectDto project = projectServiceClient.getProject(projectId);
        postValidator.validateProject(project);
        List<Post> draftsByProjectId = postRepository.findDraftsByProjectId(project.getId());
        return getSortedDrafts(draftsByProjectId);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNotDeletedPublishedPostsByAuthorId(Long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        postValidator.validateAuthor(user);
        List<Post> publishedPostsByAuthorId = postRepository.findPublishedPostsByAuthorId(user.getId());
        return getSortedPublishedPosts(publishedPostsByAuthorId);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNotDeletedPublishedPostsByProjectId(Long projectId) {
        ProjectDto project = projectServiceClient.getProject(projectId);
        postValidator.validateProject(project);
        List<Post> publishedPostsByProjectId = postRepository.findPublishedPostsByProjectId(project.getId());
        return getSortedPublishedPosts(publishedPostsByProjectId);
    }

    @Transactional
    public boolean softDeletePost(Long postId) {
        Post post = getPostById(postId);

        postValidator.validationOfPostDelete(post);

        post.setDeleted(true);
        postRepository.save(post);
        return true;
    }

    private List<PostDto> getSortedDrafts(List<Post> draftsByAuthorId) {
        return draftsByAuthorId.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> getSortedPublishedPosts(List<Post> publishedPostsByAuthorId) {
        return publishedPostsByAuthorId.stream()
                .sorted(Comparator.comparing(Post::getPublishedAt))
                .map(postMapper::toDto)
                .toList();
    }
}
