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
        if (createPostDto.getAuthorId() == null
                && createPostDto.getProjectId() != null
                && createPostDto.getProjectId() > 0) {
            log.info("Project id was provided, checking if project exists");
            projectService.checkProjectExist(createPostDto.getProjectId());
        } else if (createPostDto.getAuthorId() != null
                && createPostDto.getAuthorId() > 0
                && createPostDto.getProjectId() == null) {
            log.info("Author id was provided, checking if user exists");
            userService.checkUserExist(createPostDto.getAuthorId());
        } else {
            log.error("AuthorId or projectId were provided both or neither");
            throw new IllegalArgumentException("AuthorId or projectId were provided both or neither");
        }
    }

    public Post isPostExists(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("Post not found"));
    }
}
