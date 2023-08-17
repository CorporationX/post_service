
package faang.school.postservice.client;

import faang.school.postservice.config.postCorrecter.FeignBingSpellClientConfig;
import faang.school.postservice.dto.postCorrecter.SpellCheckDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bingSpell-service", url = "${ai-spelling.url}", configuration = FeignBingSpellClientConfig.class)
public interface BingSpellClient {

    @PostMapping
    ResponseEntity<SpellCheckDto> checkSpell(@RequestParam("mode") String mode, String body);
}
