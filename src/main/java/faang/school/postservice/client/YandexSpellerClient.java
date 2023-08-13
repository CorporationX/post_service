package faang.school.postservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "yandexSpellerClient", url = "${yandex-spell-checker-service.host}")
public interface YandexSpellerClient {
    @GetMapping("/checkText?text={text}")
    String checkText(@PathVariable String text);

    @PostMapping("/checkTexts")
    boolean checkTexts(@RequestBody List<String> texts);
}
