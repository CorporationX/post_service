package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostValidator {

    private final UserServiceClient userServiceClient;

    public void validateAuthor(PostDto postDto) {
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("The author of the post is not specified");
        }
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("A post cannot have two authors");
        }
        if (postDto.getAuthorId() != null && !userServiceClient.existById(postDto.getAuthorId())) {
            throw new DataValidationException("There is no author with this id " + postDto.getAuthorId());
        }
    }
}
