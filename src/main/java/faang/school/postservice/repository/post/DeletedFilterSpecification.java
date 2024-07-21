package faang.school.postservice.repository.post;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DeletedFilterSpecification implements PostFilterRepository{

    @Override
    public boolean isApplicable(PostFilterDto postFilterDto) {
        return postFilterDto.getDeleted() != null;
    }

    @Override
    public Specification<Post> apply(PostFilterDto postFilterDto) {
        return (root, query, builder) -> builder.equal(root.get("deleted"), postFilterDto.getDeleted());
    }
}
