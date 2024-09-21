package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.checker.UpdatedPostChecker;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper mapper;
    private final List<UpdatedPostChecker> updatedPostCheckers;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public PostDto create(PostDto postDto) {
        if (postDto.projectId() > 0) {
            checkProjectExisting(postDto.projectId());
        }
        if (postDto.authorId() > 0) {
            checkUserExisting(postDto.authorId());
        }

        Post post = mapper.toPost(postDto);
        // Если вдруг пользователь сам установил дату публикации, ставим её null
        post.setPublishedAt(null);
        return mapper.toPostDto(postRepository.save(post));
    }

    public PostDto publish(final long id) {
        Post post = getAndCheckPublishedPost(id);

        post.setPublishedAt(LocalDateTime.now());
        post.setPublished(true);
        return mapper.toPostDto(postRepository.save(post));
    }

    public PostDto update(PostDto postDto) {
        Post post = mapper.toPost(postDto);

        Post prevPost = getExistencePost(post.getId());
        updatedPostCheckers.forEach(checker -> checker.check(post, prevPost));

        return mapper.toPostDto(postRepository.save(post));
    }

    public void softlyDelete(long id) {
        Post post = getExistencePost(id);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public PostDto getPost(long id) {
        return mapper.toPostDto(getExistencePost(id));
    }

    public List<PostDto> getDraftPostsForUser(long authorId) {
        checkUserExisting(authorId);

        List<Post> posts = postRepository.findPostsByAuthorId(authorId, false);
        List<Post> sortedPosts = posts.stream()
                .sorted((firstPost, secondPost) -> secondPost.getCreatedAt().compareTo(firstPost.getCreatedAt()))
                .toList();
        return mapper.toPostDtos(sortedPosts);
    }

    public List<PostDto> getDraftPostsForProject(long projectId) {
        checkProjectExisting(projectId);

        List<Post> posts = postRepository.findPostsByProjectId(projectId, false);
        List<Post> sortedPost = posts.stream()
                .sorted((firstPost, secondPost) -> secondPost.getCreatedAt().compareTo(firstPost.getCreatedAt()))
                .toList();

        return mapper.toPostDtos(sortedPost);
    }

    public List<PostDto> getPublishedPostsForUser(long authorId) {
        checkUserExisting(authorId);

        List<Post> posts = postRepository.findPostsByAuthorId(authorId, true);
        List<Post> sortedPosts = posts.stream()
                .sorted((firstPost, secondPost) -> secondPost.getPublishedAt().compareTo(firstPost.getPublishedAt()))
                .toList();

        return mapper.toPostDtos(sortedPosts);
    }

    public List<PostDto> getPublishedPostsForProject(long projectId) {
        checkProjectExisting(projectId);

        List<Post> posts = postRepository.findPostsByProjectId(projectId, true);
        List<Post> sortedPosts = posts.stream()
                .sorted((firstPost, secondPost) -> secondPost.getPublishedAt().compareTo(firstPost.getPublishedAt()))
                .toList();

        return mapper.toPostDtos(sortedPosts);
    }

    private void checkUserExisting(long id) {
        UserDto userDto = userServiceClient.getUser(id);
        if (userDto == null) {
            String message = "Пользователь с id = " + id + " не найден в системе";
            log.error(message);
            throw new DataValidationException(message);
        }
    }

    private void checkProjectExisting(long id) {
        ProjectDto projectDto = projectServiceClient.getProject(id);
        if (projectDto == null) {
            String message = "Проект с id = " + id + " не найден в системе";
            log.error(message);
            throw new DataValidationException(message);
        }
    }

    private Post getExistencePost(long id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty() || postOpt.get().isDeleted()) {
            String message = "Пост с id = " + id + " не существует в системе";
            log.error(message);
            throw new EntityNotFoundException(message);
        }
        return postOpt.get();
    }

    private Post getAndCheckPublishedPost(long id) {
        Post post = getExistencePost(id);
        if (post.isPublished()) {
            String message = "Пост с id = " + id + " уже был опубликован";
            log.error(message);
            throw new DataValidationException(message); // не знаю какое исключение тут подобрать
        }

        return post;
    }
}
