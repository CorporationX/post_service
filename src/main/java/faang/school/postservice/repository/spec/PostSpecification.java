package faang.school.postservice.repository.spec;

import faang.school.postservice.model.Post;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@UtilityClass
public class PostSpecification {

    public static Specification<Post> filter(
            Long authorId,
            Boolean published
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get(Post.Fields.deleted)));

            addIfNotNull(predicates, authorId, () -> cb.equal(root.get(Post.Fields.authorId), authorId));
            addIfNotNull(predicates, published, () -> cb.equal(root.get(Post.Fields.published), published));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T> void addIfNotNull(List<Predicate> predicates, T value, Supplier<Predicate> predicateSupplier) {
        if (value != null) {
            predicates.add(predicateSupplier.get());
        }
    }
}
