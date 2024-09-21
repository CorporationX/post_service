package faang.school.postservice.service.post.impl.filter;

import faang.school.postservice.model.Post;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Predicate;

public class UnPublishedPostFilter implements PostFilter {
    @Override
    public Predicate<Post> getFilter() {
        return post -> !post.isPublished();
    }

    @Override
    public Function<Post, LocalDateTime> getCompareStrategy() {
        return Post::getCreatedAt;
    }
}
