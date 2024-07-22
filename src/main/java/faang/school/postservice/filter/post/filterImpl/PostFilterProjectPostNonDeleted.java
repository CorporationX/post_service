package faang.school.postservice.filter.post.filterImpl;

import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.filter.post.PostFilterDto;
import faang.school.postservice.model.Post;

import java.util.Objects;

public class PostFilterProjectPostNonDeleted implements PostFilter {
    @Override
    public boolean isApplicable(PostFilterDto filters) {
        boolean isUser = filters.userId() == null && filters.projectId() != null;
        boolean isNonDeleted = !filters.isDeleted();
        boolean isPublished = filters.isPublished();
        return isUser && isNonDeleted && isPublished;
    }

    @Override
    public boolean test(Post entity, PostFilterDto filters) {
        return Objects.equals(entity.getProjectId(), filters.projectId())
                && !entity.isDeleted()
                && entity.isPublished();
    }
}
