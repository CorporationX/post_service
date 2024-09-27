package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {


    public void validateBlankContent(PostDto post) {
        if (post.getContent().isBlank()) {
            throw new NullPointerException("Post content can't be blank");
        }
    }


    public void validatePostBeforeCreate(PostDto post) {
        if((post.getAuthorId() != null) == (post.getProjectId() != null)){
            throw new DataValidationException("The post must be filled with either the author or the project");
        }
    }
}
