package faang.school.postservice.filter.post.filterImpl;

import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PostFilterUserDraftNonDeleted implements PostFilter {
    @Override
    public boolean isApplicable(PostFilterDto filters) {
        boolean isUser = filters.userId() != null && filters.projectId() == null;
        boolean isNonDeleted = !filters.isDeleted();
        boolean isNonPublished = !filters.isPublished();
        return isUser && isNonDeleted && isNonPublished;
    }

    @Override
    public boolean test(Post entity, PostFilterDto filters) {
        return Objects.equals(entity.getAuthorId(), filters.userId())
                && !entity.isDeleted()
                && !entity.isPublished();
    }
}
