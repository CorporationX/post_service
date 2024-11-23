package faang.school.postservice.consumer;

import com.google.protobuf.InvalidProtocolBufferException;

public interface KafkaConsumer<T> {
    void processEvent(T message) throws InvalidProtocolBufferException;
}
