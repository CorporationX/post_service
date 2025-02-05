package faang.school.postservice.controller;

import faang.school.postservice.config.redis.RedisMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/test/redis")
@RestController
public class TestRedisController {

    private final RedisMessagePublisher publisher;

    @PostMapping("/user/ban")
    public ResponseEntity<Void> usersBan(@RequestBody List<Long> userIds) {
        publisher.publish("user_ban", userIds);
        return ResponseEntity.ok().build();
    }
}
