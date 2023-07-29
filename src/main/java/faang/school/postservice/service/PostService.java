package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.AlreadyPostedException;
import faang.school.postservice.exception.IncorrectIdException;
import faang.school.postservice.exception.NoPostInDataBaseException;
import faang.school.postservice.exception.SamePostAuthorException;
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

    private void validatePostId(long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoPostInDataBaseException("Данного поста не существует");
        }
    }

    private void validateData(PostDto postDto) {
        Long authorId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();

        if (authorId != null && projectId != null) {
            throw new SamePostAuthorException("Автором поста не может быть одновременно пользователь и проект");
        }
        if (authorId != null) {
            try {
                userService.getUser(authorId);
            } catch (FeignException e) {
                throw new IncorrectIdException("Данный пользователь не найден");
            }
        } else {
            try {
                projectService.getProject(projectId);
            } catch (FeignException e) {
                throw new IncorrectIdException("Данный проект не найден");
            }
        }
    }
}