package faang.school.postservice.producer;

import faang.school.postservice.event.LikePostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LikePostProducer extends AbstractProducer<LikePostEvent> {
    public LikePostProducer(List<Producer<LikePostEvent>> producers) {
        super(producers);
    }
}
