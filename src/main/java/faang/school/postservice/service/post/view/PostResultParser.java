package faang.school.postservice.service.post.view;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostResultParser {

    public List<Post> parseResult(Object result) {
        if (result instanceof Post) {
            return List.of((Post) result);
        } else if (result instanceof List<?> list) {
            return list.stream()
                    .filter(Post.class::isInstance)
                    .map(Post.class::cast)
                    .toList();
        }
        return Collections.emptyList();
    }
}
