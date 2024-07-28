package faang.school.postservice.client;

import faang.school.postservice.model.hashtag.Hashtag;
import faang.school.postservice.model.hashtag.HashtagRequest;
import faang.school.postservice.model.hashtag.HashtagResponse;
import faang.school.postservice.model.post.PostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "hashtag-service", url = "${hashtag-service.host}:${hashtag-service.port}")
public interface HashtagServiceClient {

    @PostMapping("/hashtag/list")
    void saveHashtags(@RequestBody HashtagRequest request);

    @PostMapping("/hashtag/allByNames")
    HashtagResponse getHashtagsByNames(@RequestBody HashtagRequest hashtagRequest);

    @PostMapping("/hashtag/post")
    PostResponse findPostsByHashtag(@RequestBody String hashtag);
}
