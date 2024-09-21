package faang.school.postservice.service.post.checker;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ProjectUpdatedPostChecker implements UpdatedPostChecker{

    @Override
    public void check(Post post, Post prevPost) {
        if (Objects.equals(post.getProjectId(), prevPost.getProjectId())) {
            throw new DataValidationException("Нельзя изменить автора(проект) поста");
        }
    }
}
