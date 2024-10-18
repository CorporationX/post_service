package faang.school.postservice.service.post;

import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.service.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProducerService {
  private final EventProducer<PostEvent> postProducer;
  private final EventProducer<PostViewEvent> postViewProducer;

  public void sendPostEvent(PostEvent postEvent) {
    postProducer.sendEvent(postEvent);
  }

  public void sendPostView(PostViewEvent postViewEvent) {
    postViewProducer.sendEvent(postViewEvent);
  }

}
