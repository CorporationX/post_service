
package faang.school.postservice.client;

import faang.school.postservice.dto.postCorrecter.SpellCheckDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "bingSpell-service", url = "${ai-spelling.url}")
public interface BingSpellClient {

    @PostMapping
    ResponseEntity<SpellCheckDto> checkSpell(@RequestHeader Map<String, String> headers,
                                             @RequestParam("mode") String mode, String body);
}
