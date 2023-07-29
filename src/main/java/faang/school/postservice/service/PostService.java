package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.AlreadyDeletedException;
import faang.school.postservice.exception.AlreadyPostedException;
import faang.school.postservice.exception.IncorrectIdException;
import faang.school.postservice.exception.NoPostException;
import faang.school.postservice.exception.NoPostInDataBaseException;
import faang.school.postservice.exception.NoPublishedPostException;
import faang.school.postservice.exception.NoDraftsException;
import faang.school.postservice.exception.SamePostAuthorException;
import faang.school.postservice.exception.UpdatePostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userService;
    private final ProjectServiceClient projectService;
    private final PostMapper postMapper;

    public PostDto crateDraftPost(PostDto postDto) {
        validateData(postDto);

        Post savedPost = postRepository.save(postMapper.toPost(postDto));
        return postMapper.toDto(savedPost);
    }

    public PostDto publishPost(long postId) {
        validatePostId(postId);

        List<Post> readyToPublishPost = postRepository.findReadyToPublish().stream()
                .filter(post -> post.getId() == postId)
                .toList();
        if (readyToPublishPost.isEmpty()) {
            throw new AlreadyPostedException("Нельзя опубликовать пост, который уже был опубликован или удален");
        }

        Post post = readyToPublishPost.get(0);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(post);
    }

    public PostDto updatePost(PostDto updatePost) {
        long postId = updatePost.getId();
        validatePostId(postId);

        Post post = postRepository.findById(postId).get();
        validateAuthorUpdate(post, updatePost);

        post.setContent(updatePost.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        return postMapper.toDto(post);
    }

    public PostDto softDelete(long postId) {
        validatePostId(postId);

        Post post = postRepository.findById(postId).get();

        if (post.isDeleted()) {
            throw new AlreadyDeletedException("Пост уже был удален");
        }
        post.setDeleted(true);
        return postMapper.toDto(post);
    }

    public PostDto getPost(long postId) {
        validatePostId(postId);

        Post post = postRepository.findById(postId).get();
        if (post.isDeleted()) {
            throw new AlreadyDeletedException("Данный пост был удален");
        }
        if (!post.isPublished()) {
            throw new NoPublishedPostException("Данный пост еще не был опубликован");
        }

        return postMapper.toDto(post);
    }

    public List<PostDto> getUserDrafts(long userId) {
        validateUserId(userId);

        List<PostDto> userDrafts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        if (userDrafts.isEmpty()) {
            throw new NoDraftsException("У данного пользователя нет черновиков постов");
        }
        return userDrafts;
    }

    public List<PostDto> getProjectDrafts(long projectId) {
        validateProjectId(projectId);

        List<PostDto> projectDrafts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        if (projectDrafts.isEmpty()) {
            throw new NoDraftsException("У данного проекта нет черновиков постов");
        }
        return projectDrafts;
    }

    public List<PostDto> getUserPosts(long userId) {
        validateUserId(userId);

        List<PostDto> userPosts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        if (userPosts.isEmpty()) {
            throw new NoPostException("У данного пользователя нет опубликованных постов");
        }
        return userPosts;
    }

    private void validatePostId(long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoPostInDataBaseException("Данного поста не существует");
        }
    }

    private void validateData(PostDto postDto) {
        Long userId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();

        if (userId != null && projectId != null) {
            throw new SamePostAuthorException("Автором поста не может быть одновременно пользователь и проект");
        }
        if (userId != null) {
           validateUserId(userId);
        } else {
           validateProjectId(projectId);
        }
    }

    private void validateAuthorUpdate(Post post, PostDto updatePost) {
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();
        Long updateAuthorId = updatePost.getAuthorId();
        Long updateProjectId = updatePost.getProjectId();

        if (authorId != null) {
            if (updateAuthorId == null || updateAuthorId != authorId) {
                throw new UpdatePostException("Автор поста не может быть удален или изменен");
            }
        } else {
            if (updateProjectId == null || updateProjectId != projectId) {
                throw new UpdatePostException("Автор поста не может быть удален или изменен");
            }
        }
    }

    private void validateUserId(long id) {
        try {
            userService.getUser(id);
        } catch (FeignException e) {
            throw new IncorrectIdException("Данный пользователь не найден");
        }
    }

    private void validateProjectId(long id) {
        try {
            projectService.getProject(id);
        } catch (FeignException e) {
            throw new IncorrectIdException("Данный проект не найден");
        }
    }
}