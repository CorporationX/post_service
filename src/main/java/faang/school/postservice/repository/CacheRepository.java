package faang.school.postservice.repository;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CacheRepository {
    @Autowired
    JdbcTemplate jdbc;

    public List<UserDto> getUsers(List<Long> ids) {
        if (ids.isEmpty()) return List.of();

        var sql = "SELECT id, username, email FROM \"users\" WHERE id = ANY(?)";
        return jdbc.query(sql, ps -> ps.setArray(1, ps.getConnection()
                        .createArrayOf("bigint", ids.toArray())),
                (rs, i) -> new UserDto(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3))
        );
    }

    public UserDto getUser(long userId) {
        String sql = "SELECT id, username, email FROM users WHERE id = ?";
        return jdbc.queryForObject(
                sql,
                (rs, rowNum) -> new UserDto(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email")
                ),
                userId
        );
    }
}
