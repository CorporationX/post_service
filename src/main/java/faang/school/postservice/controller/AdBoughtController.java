package faang.school.postservice.controller;

import faang.school.postservice.model.event.AdBoughtEvent;
import faang.school.postservice.publisher.AdBoughtEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Random;

/* Это просто заглушка для выполнения задачи BJS2-27276
https://faang-school.atlassian.net/jira/software/c/projects/BJS2/boards/60?assignee=600ac994bb4eb50078abb00f&selectedIssue=BJS2-27276
 */

@RequestMapping("/ad")
@RestController
@RequiredArgsConstructor
public class AdBoughtController {
    private final AdBoughtEventPublisher publisher;

    @GetMapping()
    public void getAlbumById() {
        System.out.println(111);
        Random random = new Random();
        AdBoughtEvent adBoughtEvent = new AdBoughtEvent();
        adBoughtEvent.setReceivedAt(LocalDateTime.now());
        adBoughtEvent.setAmount(random.nextLong(1000L, 10000L));
        adBoughtEvent.setAdvDuration(random.nextInt(1, 10));
        adBoughtEvent.setPostId(random.nextLong(1L, 1000L));
        adBoughtEvent.setUserId(random.nextLong(1L, 11L));
        publisher.publish(adBoughtEvent);
        System.out.println(222);
    }
}
