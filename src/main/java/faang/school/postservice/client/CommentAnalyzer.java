package faang.school.postservice.client;

import faang.school.postservice.dto.commentAnalyzer.request.CommentRequestDto;
import faang.school.postservice.dto.commentAnalyzer.response.ToxicityScoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CommentAnalyzer {
    private final WebClient commentAnalyzerClient;

    @Value("${services.comment-analyzer.api-key}")
    String apiKey;

    @Value("${services.comment-analyzer.path}")
    String commentAnalyzerPath;

    public Mono<ToxicityScoreDto> analyzeComment(String text) {
        return commentAnalyzerClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(commentAnalyzerPath)
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(new CommentRequestDto(text))
                .retrieve()
                .bodyToMono(ToxicityScoreDto.class);
    }
}
