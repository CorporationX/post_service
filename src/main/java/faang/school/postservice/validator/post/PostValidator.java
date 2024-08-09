package faang.school.postservice.validator.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.project.ProjectValidator;
import faang.school.postservice.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostValidator {
    private final ProjectValidator projectValidator;
    private final UserValidator userValidator;
    private final PostRepository postRepository;

    public void validateCreate(PostDto postDto) {
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new PostValidationException("Post can have only 1 author!");
        }

        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new PostValidationException("Post should have author!");
        }
    }

    public boolean checkIfAuthorExists(PostDto postDto) {
        Long projectId = postDto.getProjectId();
        if (projectId != null) {
            if (projectValidator.isProjectExists(projectId)) {
                return true;
            }
        }

        Long authorId = postDto.getAuthorId();
        if (authorId != null) {
            return userValidator.isUserExists(authorId);
        }

        return false;
    }


    public void validatePublish(Optional<Post> post) {
        if (post.isEmpty()) {
            throw new PostValidationException("Post with such id doesn't exists");
        }

        if (post.get().isPublished()) {
            throw new PostValidationException("Post already published!");
        }
    }

    public void validateUpdate(Long postId, PostDto postDto) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Post post = postOptional.orElseThrow(
                () -> new PostValidationException("Post with such id doesn't exists"));

        if (!Objects.equals(post.getAuthorId(), postDto.getAuthorId())
                || !Objects.equals(post.getProjectId(), postDto.getProjectId())) {
            throw new PostValidationException("You can't change author!");
        }
    }
}
