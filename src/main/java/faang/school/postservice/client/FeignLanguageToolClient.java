package faang.school.postservice.client;

import faang.school.postservice.dto.text.TextCheckResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "languageToolClient", url = "${languagetool.api-url}")
public interface FeignLanguageToolClient {

    @PostMapping(value = "/v2/check", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TextCheckResponseDto checkText(@RequestParam("text") String text,
                                   @RequestParam("language") String language);
}
