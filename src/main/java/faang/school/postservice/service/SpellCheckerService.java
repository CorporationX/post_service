package faang.school.postservice.service;

import faang.school.postservice.config.SpellCheckerConfig;
import faang.school.postservice.dto.spell.SpellCheckerResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpellCheckerService {

    private final RestTemplate restTemplate;
    private final SpellCheckerConfig spellCheckerConfig;

    @Retryable(retryFor = {HttpServerErrorException.class, UnknownHttpStatusCodeException.class},
            maxAttempts = 5, backoff = @Backoff(delay = 500L, multiplier = 2))
    public Optional<String> checkMessage(String messageToCheck) {
        URI uri = getUriForMessageChecker(messageToCheck);
        SpellCheckerResponseDto responseDto = restTemplate.getForObject(uri, SpellCheckerResponseDto.class);
        if (responseDto != null && responseDto.isStatus()) {
            return Optional.of(responseDto.getResponse().getCorrected());
        }
        return Optional.empty();
    }

    private URI getUriForMessageChecker(String messageToCheck) {
        return UriComponentsBuilder.fromHttpUrl(spellCheckerConfig.getUrl())
                .queryParam(spellCheckerConfig.getKeyParam(), spellCheckerConfig.getKey())
                .queryParam(spellCheckerConfig.getLanguageParam(), spellCheckerConfig.getLanguage())
                .queryParam(spellCheckerConfig.getTextParam(), messageToCheck)
                .build().encode().toUri();
    }
}