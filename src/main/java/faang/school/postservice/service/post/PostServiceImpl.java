package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final ProjectServiceClient projectServiceClient;
    private final PostMediaService postMediaService;

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
    @Transactional
    public PostDto createPostWithImages(long authorId, CreatePostDto createPostDto, List<MultipartFile> images) throws ValidationException {
        PostDto created = createPost(authorId, createPostDto);
        Post post = postRepository.findById(created.id())
                .orElseThrow();

        List<Resource> added = postMediaService.uploadImages(post, images);
        if (post.getResources() != null) {
            post.getResources().addAll(added);
        } else {
            post.setResources(added);
        }

        return postMapper.toPostDto(post);
    }

    @Override
    @Transactional
    public PostDto updatePostMedia(long postId, long requesterId, List<Long> removeResourceIds, List<MultipartFile> addImages) {
        Post post = postRepository.findById(postId)
                .orElseThrow();

        if (post.getAuthorId() != requesterId) {
            log.warn("Mismatch between author {} and requester {}", post.getAuthorId(), requesterId);
            throw new ForbiddenException("Mismatch between author {} and requester {}");
        }

        if (removeResourceIds != null && !removeResourceIds.isEmpty() && post.getResources() != null) {
            List<Resource> toRemove = post.getResources().stream()
                    .filter(r -> removeResourceIds.contains(r.getId()))
                    .toList();
            postMediaService.deleteResources(toRemove);
            post.getResources().removeAll(toRemove);
        }

        if (addImages != null && !addImages.isEmpty()) {
            var added = postMediaService.uploadImages(post, addImages);
            if (post.getResources() != null) post.getResources().addAll(added);
            else post.setResources(added);
        }

        return postMapper.toPostDto(post);
    }

}
