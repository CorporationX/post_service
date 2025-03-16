package faang.school.postservice.service;

import faang.school.postservice.dto.post.OwnerType;
import faang.school.postservice.dto.post.PostDTO;
import faang.school.postservice.exception.DataAlreadyDeletedException;
import faang.school.postservice.exception.DataAlreadyExistException;
import faang.school.postservice.exception.UnpublishedPostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
import faang.school.postservice.validator.PostValidator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.LongFunction;
import java.util.function.Predicate;

@Service
@Slf4j
public class PostService {
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final PostRepositoryAdapter postRepositoryAdapter;
    private final Map<OwnerType, LongFunction<List<Post>>> finders;

    public PostService(PostValidator postValidator, PostMapper postMapper,
                       PostRepositoryAdapter postRepositoryAdapter) {
        this.postValidator = postValidator;
        this.postMapper = postMapper;
        this.postRepositoryAdapter = postRepositoryAdapter;
        this.finders = Map.of(
                OwnerType.AUTHOR, postRepositoryAdapter::findByAuthorId,
                OwnerType.PROJECT, postRepositoryAdapter::findByProjectId);
    }

    public PostDTO createDraft(PostDTO draftDTO) {
        postValidator.validatedOwnerPost(draftDTO);
        Post draft = postRepositoryAdapter.save(postMapper.toEntity(draftDTO));

        log.info("Draft post was successfully created, draft id = {}", draft.getId());
        return postMapper.toDto(draft);
    }

    @Transactional
    public PostDTO publishPost(long postId) {
        Post post = postValidator.findPostWithId(postId);

        if (post.isPublished() || (post.getScheduledAt() != null &&
                post.getScheduledAt().isAfter(LocalDateTime.now()))) {
            throw new DataAlreadyExistException("This post has already been published or scheduled for publication");
        }

        if (post.isDeleted()) {
            throw new DataAlreadyDeletedException("Can not publish a remote post");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        log.info("Post was successfully published, post id = {}", postId);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDTO updatePost(PostDTO updatePost) {
        Post post = postValidator.findPostWithId(updatePost.id());
        postValidator.validateAuthorForUpdate(post, updatePost);

        post.setContent(updatePost.content());
        post.setUpdatedAt(LocalDateTime.now());

        log.info("Post was successfully updated, post id = {}", post.getId());
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDTO deletePost(long postId) {
        Post post = postValidator.findPostWithId(postId);

        if (post.isDeleted()) {
            throw new DataAlreadyDeletedException("This post was already deleted");
        }
        post.setDeleted(true);

        log.info("Post was successfully deleted, post id = {}", postId);
        return postMapper.toDto(post);
    }

    public PostDTO getPostById(long postId) {
        Post post = postValidator.findPostWithId(postId);

        if (post.isDeleted()) {
            throw new DataAlreadyDeletedException("This post was already deleted");
        }

        if (post.getScheduledAt() != null) {
            throw new UnpublishedPostException("This post unpublished yet");
        }

        log.info("Post received successfully, post id = {}", postId);
        return postMapper.toDto(post);
    }

    public List<PostDTO> getAllDraftsByAuthorId(long authorId) {
        postValidator.userOwnerOfThePost(authorId);

        List<PostDTO> allUserDrafts = getAllPostsOrDrafts(authorId, OwnerType.AUTHOR,
                post -> !post.isPublished() && !post.isDeleted());

        log.info("Drafts received successfully, author id = {}", authorId);
        return allUserDrafts;
    }

    public List<PostDTO> getAllDraftsByProjectId(long projectId) {
        postValidator.projectOwnerOfThePost(projectId);

        List<PostDTO> allProjectDrafts = getAllPostsOrDrafts(projectId, OwnerType.PROJECT,
                post -> !post.isPublished() && !post.isDeleted());

        log.info("Drafts received successfully, project id = {}", projectId);
        return allProjectDrafts;
    }

    public List<PostDTO> getAllPostsByAuthorId(long authorId) {
        postValidator.userOwnerOfThePost(authorId);

        List<PostDTO> allUserPosts = getAllPostsOrDrafts(authorId, OwnerType.AUTHOR,
                post -> post.isPublished() && !post.isDeleted());

        log.info("Post received successfully, author id = {}", authorId);
        return allUserPosts;
    }

    public List<PostDTO> getAllPostsByProjectId(long projectId) {
        postValidator.projectOwnerOfThePost(projectId);

        List<PostDTO> allProjectPosts = getAllPostsOrDrafts(projectId, OwnerType.PROJECT,
                post -> post.isPublished() && !post.isDeleted());

        log.info("Posts received successfully, project id = {}", projectId);
        return allProjectPosts;
    }

    private List<PostDTO> getAllPostsOrDrafts(long ownerId, OwnerType ownerType, Predicate<Post> filter) {
        LongFunction<List<Post>> finder = finders.get(ownerType);

        return finder.apply(ownerId)
                .stream()
                .filter(filter)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }
}
