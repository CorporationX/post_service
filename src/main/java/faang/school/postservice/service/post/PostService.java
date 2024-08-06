package faang.school.postservice.service.post;

import faang.school.postservice.api.MultipartFileMediaApi;
import faang.school.postservice.dto.media.MediaDto;
import faang.school.postservice.dto.post.GetPostsDto;
import faang.school.postservice.dto.post.UpdatablePostDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.dto.post.DraftPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.resource.UpdatableResourceDto;
import faang.school.postservice.exception.messages.ValidationExceptionMessage;
import faang.school.postservice.exception.post.UnexistentPostException;
import faang.school.postservice.exception.validation.DataValidationException;
import faang.school.postservice.mapper.post.MediaMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.mapper.post.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.post.command.UpdatePostResourceCommand;
import faang.school.postservice.service.post.validator.PostServiceValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;

    private final MultipartFileMediaApi mediaApi;

    private final UpdatePostResourceCommand updatePostResource;

    private final PostMapper postMapper;
    private final ResourceMapper resourceMapper;
    private final MediaMapper mediaMapper;

    private final PostServiceValidator validator;

    @Transactional
    public PostDto createPostDraft(DraftPostDto draft) {

        validator.validateCreatablePostDraft(draft);

        PostDto postDto = postMapper.fromDraftPostDto(draft);
        Post post = postMapper.toEntity(postDto);

        Post savedPost = postRepository.save(post);

        List<MediaDto> savedMedia = mediaApi.saveAll(draft.getMedia());

        List<Resource> creatableResource = savedMedia.stream()
                .map(m -> {
                    ResourceDto dto = mediaMapper.toResourceDto(m);
                    return resourceMapper.toEntity(savedPost.getId(), dto);
                })
                .toList();

        List<Resource> savedResources;
        if (! creatableResource.isEmpty()) {
            savedResources = resourceRepository.saveAll(creatableResource);
        } else {
            savedResources = Collections.emptyList();
        }

        savedPost.setResources(savedResources);

        return postMapper.toDto(savedPost);
    }

    @Transactional
    public PostDto publishPost(long postId) {

        Post post = getPost(postId);

        validator.validatePublishablePost(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);

        return postMapper.toDto(savedPost);
    }

    private Post getPost(@NotNull Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new UnexistentPostException(postId)
        );
    }

    @Transactional
    public PostDto updatePost(UpdatablePostDto updatablePost) {

        validator.validateUpdatablePost(updatablePost);

        Post post = getPost(updatablePost.getPostId());

        validator.verifyPostDeletion(post);

        if (updatablePost.getContent() != null) {
            post.setContent(updatablePost.getContent());
        }

        if (!post.isPublished()) {

            if (updatablePost.getScheduledAt() != null) {
                post.setScheduledAt(updatablePost.getScheduledAt());
            }

            if (updatablePost.isDeleteScheduledAt()) {
                post.setScheduledAt(null);
            }

        }

        List<UpdatableResourceDto> resource = updatablePost.getResource();
        boolean areResourceUpdated = resource != null && !resource.isEmpty();

        if (areResourceUpdated) {
            List<Resource> updatedResource = updatePostResource.execute(
                    updatablePost.getPostId(),
                    resource
            );
            post.setResources(updatedResource);
        }

        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);

        return postMapper.toDto(savedPost);
    }

    @Transactional
    public void deletePost(long postId) {

        Post post = getPost(postId);

        validator.validateDeletablePost(post);

        post.setDeleted(true);

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PostDto findPost(long postId) {

        Post post = getPost(postId);

        validator.verifyPostDeletion(post);

        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPosts(@NotNull GetPostsDto getPostsDto) {

        validator.validatePostPublisher(
                getPostsDto.getAuthorId(),
                getPostsDto.getProjectId()
        );

        Long authorId = getPostsDto.getAuthorId();
        Long projectId = getPostsDto.getProjectId();

        return switch (getPostsDto.getStatus()) {
            case POST -> {
                if (authorId != null) {
                    yield findAllPublishedAuthorPosts(authorId);
                } else if (projectId != null) {
                    yield findAllPublishedProjectPosts(projectId);
                } else {
                    throw new DataValidationException(ValidationExceptionMessage.POST_WITHOUT_PUBLISHER);
                }
            }
            case DRAFT -> {
                if (authorId != null) {
                    yield findAllAuthorDrafts(authorId);
                } else if (projectId != null) {
                    yield findAllProjectDrafts(projectId);
                } else {
                    throw new DataValidationException(ValidationExceptionMessage.POST_WITHOUT_PUBLISHER);
                }
            }
        };
    }

    private List<PostDto> findAllPublishedAuthorPosts(long authorId) {

        List<Post> authorPosts = postRepository.findByAuthorId(authorId);

        List<PostDto> postDtos = authorPosts.stream()
                .filter(p -> !p.isDeleted() && p.isPublished())
                .sorted(
                        Comparator.comparing(Post::getCreatedAt).reversed()
                )
                .map(postMapper::toDto)
                .toList();

        return postDtos;
    }

    private List<PostDto> findAllPublishedProjectPosts(long projectId) {

        List<Post> authorPosts = postRepository.findByProjectId(projectId);

        List<PostDto> postDtos = authorPosts.stream()
                .filter(p -> !p.isDeleted() && p.isPublished())
                .sorted(
                        Comparator.comparing(Post::getCreatedAt).reversed()
                )
                .map(postMapper::toDto)
                .toList();

        return postDtos;
    }

    private List<PostDto> findAllAuthorDrafts(long authorId) {
        List<Post> authorPosts = postRepository.findByAuthorId(authorId);

        List<PostDto> postDtos = authorPosts.stream()
                .filter(p -> !p.isDeleted() && !p.isPublished())
                .sorted(
                        Comparator.comparing(Post::getCreatedAt).reversed()
                )
                .map(postMapper::toDto)
                .toList();

        return postDtos;
    }

    private List<PostDto> findAllProjectDrafts(long projectId) {

        List<Post> authorPosts = postRepository.findByProjectId(projectId);

        List<PostDto> postDtos = authorPosts.stream()
                .filter(p -> !p.isDeleted() && !p.isPublished())
                .sorted(
                        Comparator.comparing(Post::getCreatedAt).reversed()
                )
                .map(postMapper::toDto)
                .toList();

        return postDtos;
    }
}
