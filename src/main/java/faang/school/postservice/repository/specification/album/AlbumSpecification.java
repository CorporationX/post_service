package faang.school.postservice.repository.specification.album;

import faang.school.postservice.model.Album;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AlbumSpecification {
    private static final String DATE_FUNCTION_NAME = "DATE";

    public static Specification<Album> getByTitle(String title) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(root.get("title"), String.format("%%%s%%", title));
    }

    public static Specification<Album> getByAuthor(Long authorId) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("authorId"), authorId);
    }

    public static Specification<Album> getByCreatedDate(LocalDate createdDate) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(criteriaBuilder
                .function(DATE_FUNCTION_NAME, LocalDate.class, root.get("createdAt")), createdDate);
    }
}
