package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.moderator.PostModerationDictionary;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModeratePostService {
    private final PostModerationDictionary postModerationDictionary;
    private final JdbcTemplate jdbcTemplate;

    @Async("executorService")
    @Transactional
    public void moderatePostBatch(List<Post> batch) {
        jdbcTemplate.batchUpdate("UPDATE post SET verified = ?, verified_date = ? WHERE id = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int j) throws SQLException {
                        Post post = batch.get(j);
                        boolean containsForbiddenWords = postModerationDictionary.containsForbiddenWordRegex(post.getContent());
                        ps.setBoolean(1, !containsForbiddenWords);
                        ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                        ps.setLong(3, post.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return batch.size();
                    }
                }
        );
    }
}
