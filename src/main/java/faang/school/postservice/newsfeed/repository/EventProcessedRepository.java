package faang.school.postservice.newsfeed.repository;

import java.util.UUID;

public interface EventProcessedRepository {
    int registerEventId(UUID uuid);
    void cleanUpOldRecords(int days);
}
