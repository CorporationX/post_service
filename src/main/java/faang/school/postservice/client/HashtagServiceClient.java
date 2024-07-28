package faang.school.postservice.client;

import faang.school.postservice.model.Hashtag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "hashtag-service", url = "${hashtag-service.host}:${hashtag-service.port}")
public interface HashtagServiceClient {

    @PostMapping("/hashtag")
    void save(String hashtagName);

    @PostMapping("/hashtag/list")
    void saveHashtags(List<String> hashtagNames);

    @GetMapping("/hashtag/allByNames")
    List<Hashtag> getHashtagsByNames(List<String> hashtagNames);

    @GetMapping("/hashtag/name")
    Hashtag getHashtagByName(String hashtagName);
}
