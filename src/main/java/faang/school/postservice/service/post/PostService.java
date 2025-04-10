package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.AmazonS3.PostImageService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class PostService {
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostImageService postImageService;

    public PostDto createDraftPost(PostDto postDto, MultipartFile[] files) {
        validateOwner(postDto.getAuthorId(), postDto.getProjectId());
        try {
            Post post = postMapper.toEntity(postDto);
            post.setAuthorId(postDto.getAuthorId());
            post.setProjectId(postDto.getProjectId());

            CompletableFuture<List<Resource>> uploadFuture = (files != null && files.length > 0)
                    ? CompletableFuture.supplyAsync(() -> postImageService.uploadImages(files))
                    : CompletableFuture.completedFuture(Collections.emptyList());

            return uploadFuture.thenApply(resources -> {
                post.setResources(resources);
                return postMapper.toDto(postRepository.save(post));
            }).exceptionally(ex -> {

                throw new DataValidationException("Failed to create post or upload images");
            }).join();

        } catch (Exception e) {
            throw new DataValidationException("Error occurred while creating draft post");
        }
    }


    @Transactional
    public PostDto publishPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("post with id " + postId + " is not exists"));

        validateDeleted(post);
        validatePublished(post, false);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(long postId, PostDto postDto, List<Long> deletedFileIds, MultipartFile[] addedFiles) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with id " + postId + " does not exist"));

        validateDeleted(post);

        postMapper.updatePostFromDto(postDto, post);

        if (deletedFileIds != null && !deletedFileIds.isEmpty()) {
            List<Resource> deleteResources = postImageService.deleteImages(deletedFileIds);
            post.getResources().removeAll(deleteResources); // Удаляем ресурсы из поста
        }

        if (addedFiles != null && addedFiles.length > 0) {
            List<Resource> addedResources = postImageService.uploadImages(addedFiles);
            post.getResources().addAll(addedResources); // Добавляем новые ресурсы к посту
        }

        postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Transactional
    public void deletePost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("post with id " + postId + " is not exists"));
        validateDeleted(post);
        post.setDeleted(true);
    }

    public PostDto getPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("post with id " + postId + " is not exists"));
        validateDeleted(post);
        validatePublished(post, true);

        return postMapper.toDto(post);
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
    }

    public boolean existsById(Long postId) {
        return postRepository.existsById(postId);
    }

    public List<PostDto> getAllAuthorDraftPosts(long authorId) {
        validExistsAuthor(authorId);
        return getAllFilterAndSortedPosts(postRepository.findByAuthorId(authorId), Predicate.not(Post::isPublished));
    }

    public List<PostDto> getAllAuthorPosts(long authorId) {
        validExistsAuthor(authorId);
        return getAllFilterAndSortedPosts(postRepository.findByAuthorId(authorId), Post::isPublished);
    }

    public List<PostDto> getAllProjectDraftPosts(long projectId) {
        validateExistsProject(projectId);
        return getAllFilterAndSortedPosts(postRepository.findByProjectId(projectId), Predicate.not(Post::isPublished));
    }

    public List<PostDto> getAllProjectPosts(long projectId) {
        validateExistsProject(projectId);
        return getAllFilterAndSortedPosts(postRepository.findByProjectId(projectId), Post::isPublished);
    }

    private List<PostDto> getAllFilterAndSortedPosts(List<Post> posts, Predicate<Post> publishedFilter) {
        return posts.stream()
                .filter(Predicate.not(Post::isDeleted))
                .filter(publishedFilter)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private void validateDeleted(Post post) {
        if (post.isDeleted()) {
            throw new DataValidationException("post with id " + post.getId() + " has been deleted.");
        }
    }

    private static void validatePublished(Post post, boolean expected) {
        if (post.isPublished() != expected) {
            throw new DataValidationException("post with id " + post.getId() +
                    (expected ? " should be published" : " has already been published"));
        }
    }

    private void validateOwner(Long authorId, Long projectId) {
        if (authorId != null && projectId != null) {
            throw new DataValidationException("author or project should be null");
        }

        if (authorId == null && projectId == null) {
            throw new DataValidationException("author or project should not be null");
        }

        validExistsAuthor(authorId);
        validateExistsProject(projectId);
    }

    private void validExistsAuthor(Long authorId) {
        if (authorId != null) {
            try {
                userServiceClient.getUser(authorId);
            } catch (FeignException e) {
                throw new EntityNotFoundException("user with id " + authorId + " is not exists");
            }

        }
    }

    private void validateExistsProject(Long projectId) {
        if (projectId != null) {
            try {
                projectServiceClient.getProject(projectId);
            } catch (FeignException e) {
                throw new EntityNotFoundException("project with id " + projectId + " is not exists");
            }
        }
    }

}
