package faang.school.postservice.validation;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validatePostDto(PostDto postDto) {
        if (postDto == null) {
            log.error("PostDto is null");
            throw new PostValidationException("postDto cannot be null");
        }

        if (postDto.content() == null || postDto.content().isBlank()) {
            log.error("PostDto content is null or empty");
            throw new PostValidationException("Post content cannot be empty");
        }

        if (postDto.authorId() == null && postDto.projectId() == null) {
            log.error("Both author ID and project ID are null.");
            throw new PostValidationException("Both author ID and project ID cannot be null at the same time.");
        }

        if (postDto.authorId() != null && postDto.projectId() != null) {
            log.error("Both author ID and project ID are non-null");
            throw new PostValidationException("Both author ID and project ID cannot be non-null at the same time.");
        }

        if (postDto.authorId() != null) {
            try {
                log.info("Calling user-service for authorId: {}", postDto.authorId());
                UserDto userDto = userServiceClient.getUser(postDto.authorId());
            } catch (FeignException.NotFound e) {
                log.error("author with ID = {} does not exist", postDto.authorId(), e);
                throw new PostValidationException("author with ID = %d does not exist".formatted(postDto.authorId()));
            }
        }


        if (postDto.projectId() != null) {
            try {
                ProjectDto projectDto = projectServiceClient.getProject(postDto.projectId());
            }catch (FeignException.NotFound e) {
                log.error("Project with ID = {} does not exist", postDto.projectId(), e);
                throw new PostValidationException("Project with ID = %d does not exist".formatted(postDto.projectId()));
            }
        }
    }
}
