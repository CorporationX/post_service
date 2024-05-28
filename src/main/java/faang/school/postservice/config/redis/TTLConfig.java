package faang.school.postservice.config.redis;

import faang.school.postservice.model.redis.AuthorCommentInRedis;
import faang.school.postservice.model.redis.AuthorPostInRedis;
import faang.school.postservice.model.redis.PostInRedis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;


public class TTLConfig extends KeyspaceConfiguration {

    @Value("${spring.data.redis.ttl}")
    private Long ttl;

    @Override
    public boolean hasSettingsFor(Class<?> type) {
        return true;
    }

    @Override
    public KeyspaceSettings getKeyspaceSettings(Class<?> type) {
        KeyspaceSettings keyspaceSettings = new KeyspaceSettings(AuthorCommentInRedis.class, "AuthorCommentInRedis");
        keyspaceSettings.setTimeToLive(ttl);
        KeyspaceSettings keyspaceSettings2 = new KeyspaceSettings(AuthorPostInRedis.class, "AuthorPostInRedis");
        keyspaceSettings2.setTimeToLive(ttl);
        KeyspaceSettings keyspaceSettings3 = new KeyspaceSettings(PostInRedis.class, "PostInRedis");
        keyspaceSettings3.setTimeToLive(ttl);
        return keyspaceSettings;
    }
}
