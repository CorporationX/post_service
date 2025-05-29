package faang.school.postservice.service.utils;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.project.ProjectService;
import faang.school.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostServiceUtils {
    private final UserService userService;
    private final ProjectService projectService;
    private final PostRepository postRepository;

    public void isAuthorOrProjectAdded(CreatePostDto createPostDto) {
        Long authorId = createPostDto.getAuthorId();
        Long projectId = createPostDto.getProjectId();

        boolean isAuthorProvidedAndValid = (authorId != null && authorId > 0);
        boolean isProjectProvidedAndValid = (projectId != null && projectId > 0);

        if (isAuthorProvidedAndValid && isProjectProvidedAndValid) {
            log.error("Both AuthorId ({}) and ProjectId ({}) were provided. Only one is allowed.", authorId, projectId);
            throw new IllegalArgumentException("Both AuthorId and ProjectId were provided. Only one is allowed.");
        } else if (isAuthorProvidedAndValid) {
            log.info("AuthorId {} was provided, checking if user exists.", authorId);
            userService.checkUserExist(authorId);
        } else if (isProjectProvidedAndValid) {
            log.info("ProjectId {} was provided, checking if project exists.", projectId);
            projectService.checkProjectExist(projectId);
        } else {
            log.error("Neither AuthorId nor ProjectId were validly provided. Exactly one positive ID is required. AuthorId: {}, ProjectId: {}", authorId, projectId);
            throw new IllegalArgumentException("Exactly one of AuthorId or ProjectId must be provided as a positive value.");
        }
    }

    public Post checkPostExists(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("Post not found"));
    }
}
