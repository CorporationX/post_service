package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.criteria.PostSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Post> findByCriteria(PostSearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getAuthorId() != null) {
            predicates.add(cb.equal(post.get("authorId"), criteria.getAuthorId()));
        }

        if (criteria.getProjectId() != null) {
            predicates.add(cb.equal(post.get("projectId"), criteria.getProjectId()));
        }

        if (criteria.getPublished() != null) {
            predicates.add(cb.equal(post.get("published"), criteria.getPublished()));
        }

        if (criteria.getDeleted() != null) {
            predicates.add(cb.equal(post.get("deleted"), criteria.getDeleted()));
        }

        query.where(predicates.toArray(new Predicate[0]));

        if (criteria.getSortField() != null) {
            Path<?> sortPath = post.get(criteria.getSortField().getField());
            if (criteria.getSortDirection() == PostSearchCriteria.SortDirection.DESC) {
                query.orderBy(cb.desc(sortPath));
            } else {
                query.orderBy(cb.asc(sortPath));
            }
        }

        TypedQuery<Post> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
}