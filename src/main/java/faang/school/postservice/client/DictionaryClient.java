package faang.school.postservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "dictionary-service", url = "${dictionary.offensive.remote.url}")
public interface DictionaryClient {

    @GetMapping("/ru")
    ResponseEntity<byte[]> getRuWords();

    @GetMapping("/en")
    ResponseEntity<byte[]> getEngWords();
}
