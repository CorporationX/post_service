package faang.school.postservice.validator;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.post.MixedAuthorshipException;
import faang.school.postservice.exception.post.NoAuthorshipException;
import faang.school.postservice.exception.post.RepeatPublishException;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.ProjectFeignService;
import faang.school.postservice.service.post.UserFeignService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final ProjectFeignService projectFeignService;
    private final UserFeignService userFeignService;

    public void validateCreate(CreatePostDto createPostDto) {
        checkNoAuthorship(createPostDto);
        checkMixedAuthorship(createPostDto);

        if (createPostDto.authorId() != null) {
            checkUserExists(createPostDto.authorId());
        }
        if (createPostDto.projectId() != null) {
            checkProjectExists(createPostDto.projectId());
        }
    }

    public void checkProjectExists(@NonNull Long projectId) {
        projectFeignService.getProjectOrFail(projectId);
    }

    public void checkUserExists(@NonNull Long authorId) {
        userFeignService.getUserOrFail(authorId);
    }

    private void checkMixedAuthorship(CreatePostDto createPostDto) {
        if (createPostDto.authorId() != null && createPostDto.projectId() != null) {
            throw new MixedAuthorshipException();
        }
    }

    private void checkNoAuthorship(CreatePostDto createPostDto) {
        if (createPostDto.authorId() == null && createPostDto.projectId() == null) {
            throw new NoAuthorshipException();
        }
    }

    public void validatePublish(@NonNull Post post) {
        if (post.isPublished()) {
            throw new RepeatPublishException("Cannot publish, post already published.");
        }
        validateNotDeleted(post);
    }

    public void validateUpdate(@NonNull Post post) {
        validateNotDeleted(post);
    }

    private void validateNotDeleted(Post post) {
        if (post.isDeleted()) {
            throw new EntityNotFoundException(String.format("No active posts with postId: %d found", post.getId()));
        }
    }

}
