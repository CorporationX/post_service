package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataAlreadyDeletedException;
import faang.school.postservice.exception.DataAlreadyExistException;
import faang.school.postservice.exception.UnpublishedPostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
import faang.school.postservice.validator.PostValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final PostRepositoryAdapter postRepositoryAdapter;
    private final PostRepository postRepository;

    public PostDto createDraft(PostDto draftDTO) {
        postValidator.validatedOwnerPost(draftDTO);
        Post draft = postRepository.save(postMapper.toEntity(draftDTO));

        log.info("Draft post was successfully created, draft id = {}", draft.getId());
        return postMapper.toDto(draft);
    }

    @Transactional
    public PostDto publishPost(long id) {
        Post post = postRepositoryAdapter.getByIdWithLikes(id);

        if (post.isPublished()
                || (post.getScheduledAt() != null && post.getScheduledAt().isAfter(LocalDateTime.now()))) {
            throw new DataAlreadyExistException("This post has already been published or scheduled for publication");
        }

        if (post.isDeleted()) {
            throw new DataAlreadyDeletedException("Can not publish a remote post");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        log.info("Post was successfully published, post id = {}", id);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(PostDto updatePost) {
        Post post = postRepositoryAdapter.getByIdWithLikes(updatePost.id());
        postValidator.validateAuthorForUpdate(post, updatePost);

        post.setContent(updatePost.content());
        post.setUpdatedAt(LocalDateTime.now());

        log.info("Post was successfully updated, post id = {}", post.getId());
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto deletePost(long id) {
        Post post = postRepositoryAdapter.getByIdWithLikes(id);

        if (post.isDeleted()) {
            throw new DataAlreadyDeletedException("This post was already deleted");
        }
        post.setDeleted(true);

        log.info("Post was successfully deleted, post id = {}", id);
        return postMapper.toDto(post);
    }

    public PostDto getPostById(long id) {
        Post post = postRepositoryAdapter.getByIdWithLikes(id);

        if (post.isDeleted()) {
            throw new DataAlreadyDeletedException("This post was already deleted");
        }

        if (post.getScheduledAt() != null) {
            throw new UnpublishedPostException("This post unpublished yet");
        }

        log.info("Post received successfully, post id = {}", id);
        return postMapper.toDto(post);
    }

    public List<PostDto> getAllDraftsByAuthorId(long authorId) {
        postValidator.getUserById(authorId);
        log.info("Drafts received successfully, author id = {}", authorId);
        return postMapper.toDtoList(postRepository.findDraftsByAuthorIdWithLikesOrderByCreationDateDesc(authorId));
    }

    public List<PostDto> getAllDraftsByProjectId(long projectId) {
        postValidator.getProjectById(projectId);
        log.info("Drafts received successfully, project id = {}", projectId);
        return postMapper.toDtoList(postRepository.findDraftsByProjectIdWithLikesOrderByCreationDateDesc(projectId));
    }

    public List<PostDto> getAllPostsByAuthorId(long authorId) {
        postValidator.getUserById(authorId);
        log.info("Post received successfully, author id = {}", authorId);
        return postMapper.toDtoList(postRepository.findByAuthorIdWithLikesOrderByPublishDateDesc(authorId));
    }

    public List<PostDto> getAllPostsByProjectId(long projectId) {
        postValidator.getProjectById(projectId);
        log.info("Posts received successfully, project id = {}", projectId);
        return postMapper.toDtoList(postRepository.findByProjectIdWithLikesOrderByPublishDateDesc(projectId));
    }
}
