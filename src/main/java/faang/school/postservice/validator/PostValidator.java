package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityWasRemovedException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostValidator {

    private static final int MAX_FILES_COUNT = 10;

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostRepository postRepository;

    public void validateContent(String content) {
        if (content.isBlank()) {
            throw new DataValidationException("Post content cannot be blank");
        }
    }

    public void validateAuthorIdAndProjectId(Long authorId, Long projectId) {
        if (authorId == null && projectId == null) {
            throw new DataValidationException("Author id and project id cannot be null at the same time");
        }
    }

    public void validateExistingPostId(Long postId) {
        if (postRepository.findById(postId).isEmpty()) {
            throw new DataValidationException("Post with id " + postId + " not found");
        }
    }

    public void validateAuthorId(Long authorId) {
        if (authorId != null && userServiceClient.getUser(authorId).getId() == null) {
            throw new DataValidationException("User with id " + authorId + " not found");
        }
    }

    public void validateProjectId(Long projectId, Long authorId) {
        if (projectId != null && projectServiceClient.getProjectById(projectId, authorId).getId() == 0) {
            throw new DataValidationException("Project with id " + projectId + " not found");
        }
    }

    public void validatePostIdOnRemoved(Long postId) {
        if (postRepository.findById(postId).get().isDeleted()) {
            throw new EntityWasRemovedException("Post with id " + postId + " was removed");
        }
    }

    public void validatePostIdOnPublished(Long postId) {
        if (postRepository.findById(postId).get().isPublished()) {
            throw new DataValidationException("Post with id " + postId + " was published");
        }
    }

    public void validatePostExistsById(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id: %s doesn't exist", postId));
        }
    }

    public void validatePostFilesCount(Post post, List<MultipartFile> files) {
        if (post.getResources().size() + files.size() > MAX_FILES_COUNT) {
            throw new DataValidationException("Post can't have more than 10 resources");
        }
    }
}