package faang.school.postservice.exception;

import faang.school.postservice.publisher.KafkaLikeEventPublisher;

public class KafkaEventPublishException extends RuntimeException {

    public KafkaEventPublishException(String message, Object... args) {
        super(String.format(message, args));
    }

}
