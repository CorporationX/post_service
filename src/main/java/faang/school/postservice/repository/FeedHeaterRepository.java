package faang.school.postservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedHeaterRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.heater.capacity.max}")
    private int maxSizePosts;

    public List<Long> findSubscriberPosts(long subscriberId) {
        String sql = String.format("SELECT p.id" +
                " FROM post p" +
                " WHERE p.author_id IN (SELECT followee_id" +
                " FROM subscription s" +
                " WHERE follower_id = %d)" +
                " AND p.published IS TRUE" +
                " AND p.deleted IS FALSE" +
                " ORDER BY p.id DESC" +
                " LIMIT %d", subscriberId, maxSizePosts);
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    public List<Long> findAllUsers() {
        return jdbcTemplate.queryForList("SELECT id FROM users WHERE active IS TRUE", Long.class);
    }
}