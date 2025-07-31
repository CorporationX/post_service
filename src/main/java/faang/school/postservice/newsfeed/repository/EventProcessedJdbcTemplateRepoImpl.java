package faang.school.postservice.newsfeed.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EventProcessedJdbcTemplateRepoImpl implements EventProcessedRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public int registerEventId(UUID uuid) {
        String sql = "INSERT INTO processed_events_uuids (uuid) VALUES (uuid) ON CONFLICT DO NOTHING";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
