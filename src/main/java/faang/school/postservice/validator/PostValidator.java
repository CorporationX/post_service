package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostValidator {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;

    public void validateAuthorIdAndProjectId(Long authorId, Long projectId) {
        UserDto userDto = userServiceClient.getUser(authorId);
        if (userDto == null) {
            String errMsg = String.format("User with ID: %d, does not exists", authorId);
            log.error(errMsg);
            throw new DataValidationException(errMsg);
        }
        if (!postRepository.existsById(projectId)) {
            log.error("Porject with ID {}, does not exist", projectId);
            throw new DataValidationException("There is no project with this ID");
        }
    }

    public void validatePublicationPost(Post post) {
        if (post.isPublished()) {
            log.error("The post has already been published.");
            throw new DataValidationException("The post has already been published.");
        }
    }
}