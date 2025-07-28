package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecifications {

    public static Specification<Post> isPublished(boolean published) {
        return (root, query, cb) -> cb.equal(root.get("published"), published);
    }

    public static Specification<Post> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<Post> byAuthorId(Long authorId) {
        return (root, query, cb) -> cb.equal(root.get("authorId"), authorId);
    }

    public static Specification<Post> byProjectId(Long projectId) {
        return (root, query, cb) -> cb.equal(root.get("projectId"), projectId);
    }

    public static Specification<Post> drafts() {
        return isPublished(false).and(isNotDeleted());
    }

    public static Specification<Post> published() {
        return isPublished(true).and(isNotDeleted());
    }

}