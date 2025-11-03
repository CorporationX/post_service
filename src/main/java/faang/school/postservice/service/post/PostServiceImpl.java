package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final ProjectServiceClient projectServiceClient;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public PostDto createPost(long authorId, CreatePostDto createPostDto) throws ValidationException {
        Post newPost = postMapper.toPost(createPostDto);
        newPost.setAuthorId(authorId);
        try {
            projectServiceClient.getProject(createPostDto.projectId());
        } catch (Exception e) {
            log.error("Проект с id: {} не существует, он не может быть автором поста.",
                    createPostDto.projectId());
            throw new DataValidationException("");
        }
        Post savedPost = postRepository.save(newPost);
        log.info("Пост успешно создан пользователем с id: {}.", authorId);
        return postMapper.toPostDto(savedPost);
    }

    @Override
    @Transactional
    public boolean publishPost(long requesterId, long postId) {
        Optional<Post> optionalPostToPublish = postRepository.findById(postId);
        if (optionalPostToPublish.isEmpty()) {
            log.error("Проект с id: {} невозможно опубликовать, он не существует.", postId);
            throw new EntityNotFoundException("");
        }
        Post postToPublish = optionalPostToPublish.get();
        if (postToPublish.isPublished()) {
            log.error("Пост с id: {} уже опубликован, его невозможно опубликовать повторно.", postId);
            throw new ForbiddenException("");
        }
        if (postToPublish.getAuthorId() != requesterId) {
            log.error("У пользователя с id: {} нет прав на публикацию поста с id: {}", requesterId, postId);
            throw new ForbiddenException("");
        }
        postToPublish.setPublished(true);
        postToPublish.setPublishedAt(LocalDateTime.now());
        postRepository.save(postToPublish);
        log.info("Пост с id: {} успешно опубликован пользователем с id: {}.", postId, requesterId);
        return true;
    }

    @Override
    @Transactional
    public PostDto updatePost(long postId, long requesterId, UpdatePostDto updatePostDto) {
        Optional<Post> optionalPostToUpdate = postRepository.findById(postId);
        if (optionalPostToUpdate.isEmpty()) {
            log.error("Пост с id: {} не может быть изменен, его не существует.", postId);
            throw new EntityNotFoundException("");
        }
        Post postToUpdate = optionalPostToUpdate.get();
        if (requesterId != postToUpdate.getAuthorId()) {
            log.error("Пользователь с id: {} не имеет прав на изменение поста с id: {}",
                    requesterId, updatePostDto.id());
            throw new ForbiddenException("");
        }
        postMapper.updatePostDto(updatePostDto, postToUpdate);
        postToUpdate.setAuthorId(requesterId);
        postRepository.save(postToUpdate);
        log.info("Пост с id: {} успешно обновлен пользователем с id: {}.", postId, requesterId);
        return postMapper.toPostDto(postToUpdate);
    }

    @Override
    @Transactional
    public boolean deletePost(long requesterId, long postId) {
        Optional<Post> optionalPostToDelete = postRepository.findById(postId);
        if (optionalPostToDelete.isEmpty() || optionalPostToDelete.get().isDeleted()) {
            log.error("Проект с id: {} невозможно удалить, он не существует.", postId);
            throw new EntityNotFoundException("");
        }
        Post postToDelete = optionalPostToDelete.get();
        if (postToDelete.getAuthorId() != requesterId) {
            log.error("У пользователя с id: {} нет прав на удаление поста с id: {}", requesterId, postId);
            throw new ForbiddenException("");
        }
        postToDelete.setDeleted(true);
        postRepository.save(postToDelete);
        log.info("Пост с id: {} успешно удален пользователем с id: {}.", postId, requesterId);
        return true;
    }

    @Override
    public PostDto getPostById(long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            log.error("Пост с id: {} невозможно посмотреть, он не существует.", postId);
            throw new EntityNotFoundException("");
        }
        return postMapper.toPostDto(optionalPost.get());
    }

    @Override
    public List<PostDto> getAllUnpublishedPostsByAuthor(long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId)
                .stream()
                .filter((post) -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
        return postMapper.toListPostDto(posts);
    }

    @Override
    public List<PostDto> getAllUnpublishedPostsByProject(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId)
                .stream()
                .filter((post) -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
        return postMapper.toListPostDto(posts);
    }

    @Override
    public List<PostDto> getAllPublishedPostsByAuthor(long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId)
                .stream()
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
        return postMapper.toListPostDto(posts);
    }

    @Override
    public List<PostDto> getAllPublishedPostsByProject(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId)
                .stream()
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
        return postMapper.toListPostDto(posts);
    }

    @Override
    public List<Long> selectUsersForBan() {
        int minViolationsForBan = 5;
        return StreamSupport
                .stream(commentRepository.findAll().spliterator(), false)
                .filter(comment -> comment.getVerified() == false)
                .collect(Collectors.groupingBy(Comment::getAuthorId, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(violator -> violator.getValue() > minViolationsForBan)
                .map(Map.Entry::getKey)
                .toList();
    }
}
