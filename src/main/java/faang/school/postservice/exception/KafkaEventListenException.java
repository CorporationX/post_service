package faang.school.postservice.exception;

import faang.school.postservice.publisher.KafkaLikeEventPublisher;

public class KafkaEventListenException extends RuntimeException {

    public KafkaEventListenException(String message, Object... args) {
        super(String.format(message, args));
    }

}