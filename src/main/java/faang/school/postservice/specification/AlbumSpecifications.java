package faang.school.postservice.specification;

import faang.school.postservice.model.Album;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AlbumSpecifications {

    public static Specification<Album> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Album> hasDescription(String description) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("description"), "%" + description + "%");
    }

    public static Specification<Album> hasAuthorId(Long authorId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("authorId"), authorId);
    }

    public static Specification<Album> createdAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Album> createdBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), date);
    }
}

