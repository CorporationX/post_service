package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;


    public List<PostDto> getPostsNotDeletedNotPublishedByUserId(long userId) {
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !(post.isDeleted()) && !(post.isPublished()))
                .map((postMapper::toDto))
                .sorted(Comparator.comparing((PostDto::getCreatedAt)))
                .toList();
    }


    public List<PostDto> getPostsNotDeletedNotPublishedByProjectId(long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map((postMapper::toDto))
                .sorted(Comparator.comparing((PostDto::getCreatedAt)))
                .toList();
    }

    public List<PostDto> getPostsPublishedNotDeletedByUserId(long userId) {
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map((postMapper::toDto))
                .sorted(Comparator.comparing((PostDto::getPublishedAt)))
                .toList();
    }

    public List<PostDto> getPostsPublishedNotDeletedByProjectId(long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map((postMapper::toDto))
                .sorted(Comparator.comparing((PostDto::getPublishedAt)))
                .toList();
    }


    public PostDto createPostDraft(@NotNull PostDto post) {
        postValidator.validatePostBeforeCreate(post);
        Post postToCreate = postMapper.toEntity(post);
        return postMapper.toDto(postRepository.save(postToCreate));
    }

    public void publishPost(long postId) {
        Post postToPublish = getPostEntity(postId);
        if (!postToPublish.isPublished()) {
            postToPublish.setPublished(true);
            postToPublish.setPublishedAt(LocalDateTime.now());
            postRepository.save(postToPublish);
        }
    }

    public PostDto updatePost(@NotNull PostDto postDto) {
        postValidator.validateBlankContent(postDto);
        Post postToUpdate = getPostEntity(postDto.getId());
        postToUpdate.setContent(postDto.getContent());
        return postMapper.toDto(postRepository.save(postToUpdate));
    }

    public void softDeletePost(long postId) {
        Post postToDelete = getPostEntity(postId);
        if (!postToDelete.isDeleted()) {
            postToDelete.setDeleted(true);
            postRepository.save(postToDelete);
        }
    }

    public PostDto getPost(long postId) {
        return postMapper.toDto(getPostEntity(postId));
    }

    private Post getPostEntity(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with id " + postId + " doesn't exist."));
    }
}
