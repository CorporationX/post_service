package faang.school.postservice.filter.post;

import faang.school.postservice.model.Post;

public interface PostFilter {
    boolean isApplicable(PostFilterDto filters);

    boolean test(Post entity, PostFilterDto filters);
}
