package faang.school.postservice.controller.aiIntegration;

import faang.school.postservice.client.BingSpellCheckingClient;
import faang.school.postservice.dto.postCorrecter.PostCorrecterDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostCorrecter {

    private final PostService postService;
    private final BingSpellCheckingClient bingSpellCheckingClient;

    @Scheduled(cron = "${postCorrecter.correcter.cron}")
    public void correctPostsScheduled() {
        //postService.correctUnpublishedPosts();
    }

    @GetMapping("/correctPost")
    public PostCorrecterDto getCorrectPost() {
        PostCorrecterDto postCorrecterDto = new PostCorrecterDto();
        postCorrecterDto.setText("Text=Thes%20is%20icorrect%20text");
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/x-www-form-urlencoded");
        headers.put("X-RapidAPI-Key", "871e222f4dmsha404f4603a4687ep188b05jsn4942910dec51");
        headers.put("X-RapidAPI-Host", "bing-spell-check2.p.rapidapi.com");

        String body = "Text=Thes is icorrect text";

        var stringHttpResponse = bingSpellCheckingClient
                .makeTextCorrect(headers, "spell", body);
        postCorrecterDto.setText(stringHttpResponse.getBody());
        return postCorrecterDto;
    }
}
