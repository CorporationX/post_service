package faang.school.postservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getSubscribers(long followeeId) {
        return jdbcTemplate.queryForList("SELECT follower_id FROM subscription WHERE followee_id = ?", Long.class, followeeId);
    }
}
