package faang.school.postservice.repository.post;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import org.springframework.data.jpa.domain.Specification;

public interface PostFilterRepository {

    boolean isApplicable(PostFilterDto postFilterDto);

    Specification<Post> apply(PostFilterDto postFilterDto);
}
