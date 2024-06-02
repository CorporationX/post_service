package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final ResourceService resourceService;

    @Transactional
    public PostDto createDraftPost(PostDto postDto, List<MultipartFile> files) {
        UserDto author = userServiceClient.getUser(postDto.getAuthorId());
        ProjectDto project = projectServiceClient.getProject(postDto.getProjectId());

        postValidator.validateAuthorExist(author, project);

        Post saved = postRepository.save(postMapper.toEntity(postDto));

        if (Objects.nonNull(files) && !files.isEmpty()) {
            List<Resource> resources = resourceService.createResourceToPost(files, saved);
            saved.setResources(resources);
        }

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
    public PostDto updatePost(PostDto postDto, Long id, List<MultipartFile> files) {
        Post post = getById(id);
        postValidator.validateChangeAuthor(post, postDto);

        post.setContent(postDto.getContent());

        removeUnnecessaryResources(post, postDto);

        Post updatedPost = postRepository.save(post);
        return postMapper.toDto(post);
    }

    private void removeUnnecessaryResources(Post post, PostDto postDto) {
        List<Long> resourceIdsFromDto = Optional.ofNullable(postDto.getResourceIds())
                .orElse(new ArrayList<>());

        List<Resource> resourcesToDelete = post.getResources().stream()
                .filter(resource -> !resourceIdsFromDto.contains(resource.getId()))
                .toList();

        post.getResources().removeAll(resourcesToDelete);
        resourceService.deleteResources(resourcesToDelete.stream()
                .map(Resource::getId)
                .toList()
        );
    }

    private PostDto createResourcesAndGetPostDto(Post post, List<MultipartFile> files) {
        if (files == null) {
            return postMapper.toDto(post);
        }

        List<Resource> savedResources = resourceService.createResourceToPost(files, post);
        List<Resource> resourcesByPost = post.getResources();

        resourcesByPost.addAll(savedResources);

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
                .orElseThrow(() -> new DataValidationException("Post with id " + id + " do not exist"));
    }
}
