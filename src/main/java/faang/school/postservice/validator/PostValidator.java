package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import org.springframework.stereotype.Component;

@Component
public class PostValidator {

    public void validatePostContent(PostDto postDto) {
        String postContent = postDto.getContent();
        if(postContent.isEmpty() || postContent.isBlank()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
    }
}
