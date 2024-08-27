package faang.school.postservice.client;

import faang.school.postservice.dto.hashtag.HashtagRequest;
import faang.school.postservice.dto.hashtag.HashtagResponse;
import faang.school.postservice.dto.post.PostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "hashtag-service", url = "${hashtag-service.host}:${hashtag-service.port}")
public interface HashtagServiceClient {

    @PostMapping("/hashtag/list")
    void saveHashtags(@RequestBody HashtagRequest request);

    @GetMapping("/hashtag/allByNames")
    HashtagResponse getHashtagsByNames(@RequestBody HashtagRequest hashtagRequest);

    @PostMapping("/hashtag/post")
    PostResponse findPostsByHashtag(@RequestBody String hashtag);
}
