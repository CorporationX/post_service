package faang.school.postservice.config.kafka.test;

import faang.school.postservice.service.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

  private final EventProducer<TestEvent> producer;

  @PostMapping("/")
  public void create(TestEvent event) {
    System.out.println("create: publish");
    producer.sendEvent(event);
  }

  @GetMapping("/{name}")
  public TestEvent get(@PathVariable("name") String name) {
    System.out.println("Get: " + name);
    return new TestEvent(name, "someText");
  }
}

