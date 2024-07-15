package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataOperationException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static faang.school.postservice.exception.message.PostOperationExceptionMessage.RE_DELETING_POST_EXCEPTION;
import static faang.school.postservice.exception.message.PostOperationExceptionMessage.RE_PUBLISHING_POST_EXCEPTION;
import static faang.school.postservice.exception.message.PostValidationExceptionMessage.NON_EXISTING_POST_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostVerifier postVerifier;

    public PostDto createPost(@Valid PostDto postDto) {
        postVerifier.verifyAuthorExistence(postDto.getAuthorId(), postDto.getProjectId());

        Post postDraft = postMapper.toEntity(postDto);
        postDraft.setPublished(false);
        postDraft.setDeleted(false);

        return postMapper.toDto(postRepository.save(postDraft));
    }

    public PostDto publishPost(long postId) {
        Post postToBePublished = getPost(postId);

        if (postToBePublished.isPublished()) {
            throw new DataOperationException(RE_PUBLISHING_POST_EXCEPTION.getMessage());
        }

        postToBePublished.setPublished(true);
        postToBePublished.setPublishedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(postToBePublished));
    }

    public PostDto updatePost(PostDto postDto) {
        postVerifier.verifyAuthorExistence(postDto.getAuthorId(), postDto.getProjectId());

        Post postToBeUpdated = getPost(postDto.getId());

        postVerifier.verifyPostMatchingSystem(postDto, postToBeUpdated);

        postToBeUpdated.setContent(postDto.getContent());
        return postMapper.toDto(postRepository.save(postToBeUpdated));
    }

    public List<Post> updatePosts(List<Post> posts) {
        return postRepository.saveAll(posts);
    }

    public void deletePost(long postId) {
        Post postToBeDeleted = getPost(postId);

        if (postToBeDeleted.isDeleted()) {
            throw new DataValidationException(RE_DELETING_POST_EXCEPTION.getMessage());
        }

        postToBeDeleted.setDeleted(true);

        postMapper.toDto(postRepository.save(postToBeDeleted));
    }

    public PostDto getPostById(long postId) {
        return postMapper.toDto(getPost(postId));
    }


    public List<Post> getAllDrafts() {
        return postRepository.findAllDrafts();
    }

    public List<PostDto> getDraftsOfUser(long userId) {
        postVerifier.verifyUserExistence(userId);

        return getSortedDrafts(postRepository.findByAuthorId(userId));
    }

    public List<PostDto> getDraftsOfProject(long projectId) {
        postVerifier.verifyProjectExistence(projectId);

        return getSortedDrafts(postRepository.findByProjectId(projectId));
    }

    public List<PostDto> getPostsOfUser(long userId) {
        postVerifier.verifyUserExistence(userId);

        return getSortedPosts(postRepository.findByAuthorId(userId));
    }

    public List<PostDto> getPostsOfProject(long projectId) {
        postVerifier.verifyProjectExistence(projectId);

        return getSortedPosts(postRepository.findByProjectId(projectId));
    }

    private Post getPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException(NON_EXISTING_POST_EXCEPTION.getMessage()));
    }

    private List<PostDto> getSortedDrafts(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> getSortedPosts(List<Post> posts) {
        return posts.stream()
                .filter(Post::isPublished)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }
}
