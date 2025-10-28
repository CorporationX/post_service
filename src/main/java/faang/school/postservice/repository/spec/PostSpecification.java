package faang.school.postservice.repository.spec;

import faang.school.postservice.model.Post;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PostSpecification {

    public static Specification<Post> filter(
            Long authorId,
            Long projectId,
            Boolean published
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("deleted")));

            if (authorId != null) {
                predicates.add(cb.equal(root.get("authorId"), authorId));
            }

            if (projectId != null) {
                predicates.add(cb.equal(root.get("projectId"), projectId));
            }

            if (published != null) {
                predicates.add(cb.equal(root.get("published"), published));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
