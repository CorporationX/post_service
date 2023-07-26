package faang.school.postservice.util.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.util.exception.CreatePostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostServiceValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validatePost(PostDto dto) {
        if (dto.getAuthorId() != null && dto.getProjectId() != null ||
                dto.getAuthorId() == null && dto.getProjectId() == null) {
            throw new CreatePostException("There is should be only one author");
        }
        if (dto.getAuthorId() != null) {
            userServiceClient.getUser(dto.getAuthorId()); // если такого пользователя или эндпоинта нет, то выбросит FeignException, я его поймаю в ExceptionHandler
        }
        if (dto.getProjectId() != null) {
            projectServiceClient.getProject(dto.getProjectId());
        }
    }
}
