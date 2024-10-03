package faang.school.postservice.service.post.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.request.PostCreationRequest;
import faang.school.postservice.dto.post.request.PostUpdatingRequest;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.post.PostCreator;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.post.impl.filter.PostFilter;
import faang.school.postservice.service.post.impl.filter.PublishedPostFilter;
import faang.school.postservice.service.post.impl.filter.UnPublishedPostFilter;
import faang.school.postservice.service.resource.ResourceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ResourceService resourceService;
    private final UserServiceClient userClient;
    private final ProjectServiceClient projectClient;

    @Override
    @Transactional
    public PostDto create(PostCreationRequest request) {
        Post post = postMapper.toPostFromCreationRequest(request);
        if (request.authorId() != null) {
            UserDto userDto = userClient.getUser(request.authorId());
            if (userDto == null) {
                throw new EntityNotFoundException("User with id " + request.authorId() + " not found");
            }
        }
        if (request.projectId() != null) {
            ProjectDto projectDto = projectClient.getProject(request.projectId());
            if (projectDto == null) {
                throw new EntityNotFoundException("Project with id " + request.projectId() + " not found");
            }
        }
        postRepository.save(post);
        if (request.filesToAdd() != null) {
            var resources = resourceService.addResourcesToPost(request.filesToAdd(), post);
            post.setResources(resources);
        }
        log.info("Created post: {}", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    public PostDto publish(Long id) {
        Post post = getPost(id);
        if (post.isPublished()) {
            throw new PostAlreadyPublishedException("Post is already published with id: " + id);
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
        log.info("Published post: {}", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    @Transactional
    public PostDto update(Long id, PostUpdatingRequest request) {
        Post post = getPost(id);
        post.setContent(request.content());
        updatePostResources(post, request);
        postRepository.save(post);
        log.info("Updated post: {}", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    public PostDto remove(Long id) {
        Post post = getPost(id);
        post.setDeleted(true);
        deleteResourcesFromPost(post.getResources().stream()
                .map(Resource::getId)
                .toList(), post);
        postRepository.save(post);
        log.info("Removed post: {}", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = getPost(id);
        log.debug("Found post: {}", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    public List<PostDto> getPostsByCreatorAndPublishedStatus(Long creatorId,
                                                             PostCreator creator, Boolean publishedStatus) {
        List<Post> posts = getPostsByCreatorId(creatorId, creator);
        PostFilter filter = getPostFilter(publishedStatus);
        posts = applyFilterToPosts(posts, filter);
        log.debug("Found {} posts by {} with id {} with published - {}",
                posts.size(), creator, creatorId, publishedStatus);
        return postMapper.toPostDtoList(posts);
    }

    private List<Post> getPostsByCreatorId(Long creatorId, PostCreator creator) {
        return switch (creator) {
            case AUTHOR -> postRepository.findByAuthorId(creatorId);
            case PROJECT -> postRepository.findByProjectId(creatorId);
        };
    }

    private PostFilter getPostFilter(Boolean publishedStatus) {
        return publishedStatus ? new PublishedPostFilter() : new UnPublishedPostFilter();
    }

    private List<Post> applyFilterToPosts(List<Post> posts, PostFilter filter) {
        return posts.stream()
                .filter(post -> !post.isDeleted())
                .filter(filter.getFilter())
                .sorted(Comparator.comparing(filter.getCompareStrategy()).reversed())
                .toList();
    }

    private Post getPost(Long id) {
        return postRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));
    }

    private void updatePostResources(Post post, PostUpdatingRequest request) {
        checkResourcesCountAfterUpdate(post, request);
        addResourcesToPost(request.filesToAdd(), post);
        if (request.filesToDeleteIds() != null) {
            deleteResourcesFromPost(request.filesToDeleteIds(), post);
        }
    }

    private void deleteResourcesFromPost(List<Long> filesToDeleteIds, Post post) {
        if (filesToDeleteIds != null) {
            checkResourcesToDeleteCount(post, filesToDeleteIds);
            resourceService.deleteResourcesFromPost(filesToDeleteIds, post);
        }
    }

    private void addResourcesToPost(List<MultipartFile> filesToAdd, Post post) {
        if (filesToAdd != null) {
            resourceService.addResourcesToPost(filesToAdd, post);
        }
    }

    private void checkResourcesToDeleteCount(Post post, List<Long> filesToDeleteIds) {
        if (filesToDeleteIds.size() > post.getResources().size()) {
            throw new IllegalArgumentException("Can't delete more resources than exist in post with id %d"
                    .formatted(post.getId()));
        }
    }

    private void checkResourcesCountAfterUpdate(
            Post post, PostUpdatingRequest request) {
        int resourceToDeleteCount = request.filesToDeleteIds() == null ? 0 : request.filesToDeleteIds().size();
        int resourceToAddCount = request.filesToAdd() == null ? 0 : request.filesToAdd().size();
        int resourcesCountAfterUpdate =
                post.getResources().size() - resourceToDeleteCount + resourceToAddCount;
        if (resourcesCountAfterUpdate > 10) {
            throw new IllegalArgumentException("Can't add more than 10 resources to post with id %d"
                    .formatted(post.getId()));
        }
    }
}