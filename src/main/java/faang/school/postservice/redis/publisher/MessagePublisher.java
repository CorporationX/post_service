package faang.school.postservice.redis.publisher;

import faang.school.postservice.redis.publisher.dto.AuthorBanDto;

public interface MessagePublisher {
    void publish(AuthorBanDto dto);
}
