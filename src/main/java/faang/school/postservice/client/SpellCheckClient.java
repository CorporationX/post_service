package faang.school.postservice.client;

import faang.school.postservice.config.spellcheck.SpellCheckFeignConfig;
import faang.school.postservice.dto.spellcheck.SpellCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "spellingCheckClient",
        url = "${textgears.url}",
        configuration = SpellCheckFeignConfig.class
)
public interface SpellCheckClient {

    @PostMapping(value = "/correct", consumes = "application/x-www-form-urlencoded")
    SpellCheckResponse checkText(
            @RequestParam("text") String text,
            @RequestParam("language") String language
    );
}
