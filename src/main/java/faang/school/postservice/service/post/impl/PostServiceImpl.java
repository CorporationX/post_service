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
import faang.school.postservice.model.post.PostCreator;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.post.PostContentVerifier;
import faang.school.postservice.service.post.impl.filter.PostFilter;
import faang.school.postservice.service.post.impl.filter.PublishedPostFilter;
import faang.school.postservice.service.post.impl.filter.UnPublishedPostFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userClient;
    private final ProjectServiceClient projectClient;
    private final PostContentVerifier postServiceAsync;
    @Value("${post.moderator.post-batch-size}")
    private int postBatchSize;
    @Value(("{post.constants.post-max-size}"))
    private int postMaxSize;

    @Override
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
    public PostDto update(Long id, PostUpdatingRequest request) {
        Post post = getPost(id);
        post.setContent(request.content());
        postRepository.save(post);
        log.info("Updated post: {}", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    public PostDto remove(Long id) {
        Post post = getPost(id);
        post.setDeleted(true);
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

    @Override
    public void moderatePosts() {
        List<Post> posts = postRepository.findNotVerified().stream()
                .limit(postMaxSize)
                .toList();
        log.info("Number of found posts for moderation: {}", posts.size());
        ListUtils.partition(posts, postBatchSize).forEach(postServiceAsync::verifyPosts);
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
}