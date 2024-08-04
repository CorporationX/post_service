package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private static final String UPDATE_IS_VERIFIED_AND_VERIFIED_DATE = """
            UPDATE post
            SET is_verified = :isVerified,
                verified_date = :verifiedDate
            WHERE id = :id
            """;

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public int[] updateVerifiedInfo(List<Post> posts) {
        MapSqlParameterSource[] args = posts.stream()
                .map(post -> new MapSqlParameterSource()
                        .addValue("isVerified", post.isVerified())
                        .addValue("verifiedDate", Timestamp.from(post.getVerifiedDate()))
                        .addValue("id", post.getId()))
                .toArray(MapSqlParameterSource[]::new);
        return namedJdbcTemplate.batchUpdate(UPDATE_IS_VERIFIED_AND_VERIFIED_DATE, args);
    }
}
