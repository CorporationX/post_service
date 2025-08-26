package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostQueryService {

    @PersistenceContext
    private EntityManager em;

    public List<Post> findLatestPublishedByAuthors(List<Long> authorIds, int limit) {
        if (authorIds == null || authorIds.isEmpty() || limit <= 0) {
            return List.of();
        }
        return em.createQuery(
                        "SELECT p FROM Post p " +
                                "WHERE p.published = true AND p.authorId IN :authorIds " +
                                "ORDER BY p.publishedAt DESC", Post.class)
                .setParameter("authorIds", authorIds)
                .setMaxResults(limit)
                .getResultList();
    }
}
