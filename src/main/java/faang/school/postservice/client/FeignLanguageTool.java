package faang.school.postservice.client;

import faang.school.postservice.dto.text.TextCheckResponseDto;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "feignlanguagetool", url = "https://api.languagetool.org")
public interface FeignLanguageTool {

    @PostMapping(value = "/v2/check", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Headers("No-Interceptor: true")
    TextCheckResponseDto checkText(@RequestParam("text") String text,
                                   @RequestParam("language") String language);
}
