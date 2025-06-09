package faang.school.postservice.client;

import faang.school.postservice.dto.LanguageToolClientResponseDto;
import feign.FeignException;
import feign.Headers;
import feign.RetryableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "language-tool",
        url = "https://${services.language-tool.domain}/${services.language-tool.version}",
        configuration = FeignConfig.class)
public interface LanguageToolClient {

    @Retryable(
            retryFor = { FeignException.class, RetryableException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @PostMapping(value = "/check", consumes = "application/x-www-form-urlencoded")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    LanguageToolClientResponseDto checkSpelling(@RequestParam("text") String text,
                                                @RequestParam("language") String language);
}
