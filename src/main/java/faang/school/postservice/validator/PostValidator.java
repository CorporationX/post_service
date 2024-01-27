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

    public void validateOwnerPost(PostDto postDto) {
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new IllegalArgumentException("Post cannot belong to both author and project");
        }
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new IllegalArgumentException("Post must belong to either author or project");
        }
    }
}
