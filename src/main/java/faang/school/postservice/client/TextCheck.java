package faang.school.postservice.client;

import faang.school.postservice.dto.text.TextResponseDto;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "languagetool", url = "https://api.languagetool.org")
public interface TextCheck {
 //for PR
    @GetMapping("/v2/check")
    @Headers("No-Interceptor: true")
    TextResponseDto checkText(@RequestParam("text") String text,
                              @RequestParam("language") String language);
}
