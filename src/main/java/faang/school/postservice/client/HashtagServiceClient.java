package faang.school.postservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "hashtag-service", url = "${hashtag-service.host}:${hashtag-service.port}")
public interface HashtagServiceClient {

    @GetMapping("/hashtags/{postId}")
    List<Long> getHashtagsIdsByPostId(@PathVariable Long postId);

    @GetMapping("/hashtags/posts")
    Map<Long, List<Long>> getHashtagsIdsByPostIds(@RequestParam List<Long> postIds);
}
