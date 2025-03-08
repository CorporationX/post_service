package faang.school.postservice.repository.specification.album;

import faang.school.postservice.model.Album;
import faang.school.postservice.model.FavoriteAlbum;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class FavoriteAlbumSpecification {

    private static final String DATE_FUNCTION_NAME = "DATE";

    public static Specification<FavoriteAlbum> getByTitle(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("album").get("title"), title);
    }

    public static Specification<FavoriteAlbum> getByAuthor(Long authorId) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("userId"), authorId);
    }

    public static Specification<FavoriteAlbum> getByCreatedDate(LocalDate createdDate) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(criteriaBuilder
                .function(DATE_FUNCTION_NAME, LocalDate.class,
                        root.get("album").get("createdAt")), createdDate);
    }
}