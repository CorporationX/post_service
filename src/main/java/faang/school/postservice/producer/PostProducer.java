package faang.school.postservice.producer;

import faang.school.postservice.event.PostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostProducer extends AbstractProducer<PostEvent> {
    public PostProducer(List<Producer<PostEvent>> producers) {
        super(producers);
    }
}
