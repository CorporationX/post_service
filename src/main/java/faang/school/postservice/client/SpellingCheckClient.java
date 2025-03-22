package faang.school.postservice.client;

import faang.school.postservice.dto.spellcheck.AiTextRequestDto;
import faang.school.postservice.dto.spellcheck.AiTextResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "spellingCheckClient", url = "https://api.textgears.com")
public interface SpellingCheckClient {

    @PostMapping("/spellcheck")
    AiTextResponseDto checkText(@RequestBody AiTextRequestDto request);
}