package faang.school.postservice.repository;

import faang.school.postservice.model.redis.UserRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRedis findUserById(long userId){
        return jdbcTemplate.queryForObject("SELECT id, username FROM users WHERE id = ? AND active is true",
                (rs, rowNum) ->
                UserRedis.builder()
                        .id(rs.getLong("id"))
                        .username(rs.getString("username"))
                        .build(), userId);
    }
}
