package faang.school.postservice.service.post.impl.filter;

import faang.school.postservice.model.Post;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Predicate;

public interface PostFilter {
    Predicate<Post> getFilter();

    Function<Post, LocalDateTime> getCompareStrategy();
}
