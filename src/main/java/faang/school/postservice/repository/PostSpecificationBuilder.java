package faang.school.postservice.repository;

import faang.school.postservice.dto.post.AuthorFilter;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.enums.AuthorType;
import faang.school.postservice.model.enums.PostStatus;
import faang.school.postservice.model.enums.SortType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builder спецификаций для фильтрации сущностей Post ({@link Post}).
 * <p>
 * Предоставляет статические методы для создания критериев фильтрации
 * и сортировки постов. Поддерживает фильтрацию по автору, статусу,
 * признаку удаления, а также сортировку по различным полям.
 * Дефолтной сортировкой (если не указана конкретная) является {@code DESC} (см. {@link SortType})
 * </p>
 *
 * @see Specification
 * @see Sort
 */
public class PostSpecificationBuilder {
    public static Specification<Post> buildSpecification(PostFilterDto filter) {
        return Specification.where(byAuthorId(filter.author()))
                .and(byStatus(filter.status()))
                .and(byDeleted(filter.includeDeleted()));
    }

    public static Specification<Post> byAuthorId(AuthorFilter author) {
        if (author == null) {
            return null;
        }

        return (root, query, cb) -> {
            String field = author.type() == AuthorType.USER ? "authorId" : "projectId";
            return cb.equal(root.get(field), author.id());
        };
    }

    public static Specification<Post> byStatus(PostStatus status) {
        return status == null ? null :
                (root, query, cb) -> cb.equal(root.get("published"), status == PostStatus.PUBLISHED);
    }

    public static Sort buildSort(PostFilterDto filter) {
        String fieldName = filter.getSortFieldName();

        if (filter.sort() == null) {
            return Sort.by(fieldName).descending();
        }

        return filter.sort() == SortType.ASC
                ? Sort.by(fieldName).ascending()
                : Sort.by(fieldName).descending();
    }

    public static Specification<Post> byDeleted(Boolean isDeleted) {
        return isDeleted == null ? null :
                ((root, query, cb) -> cb.equal(root.get("deleted"), isDeleted));
    }
}