package faang.school.postservice.repository;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Post_;
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
        return Specification.allOf(
                byUserId(filter.userId()),
                byProjectId(filter.projectId()),
                byStatus(filter.status()),
                byDeleted(filter.includeDeleted())
        );
    }

    public static Specification<Post> byUserId(Long userId) {
        if (userId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get(Post_.AUTHOR_ID), userId);
    }

    public static Specification<Post> byProjectId(Long projectId) {
        if (projectId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get(Post_.PROJECT_ID), projectId);
    }

    public static Specification<Post> byStatus(PostStatus status) {
        return status == null ? null :
                (root, query, cb) -> cb.equal(root.get(Post_.PUBLISHED), status == PostStatus.PUBLISHED);
    }

    public static Specification<Post> byDeleted(Boolean isDeleted) {
        return isDeleted == null ? null :
                ((root, query, cb) -> cb.equal(root.get(Post_.DELETED), isDeleted));
    }
}