package faang.school.postservice.controller.heater;

import faang.school.postservice.service.heater.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/heat")
@RequiredArgsConstructor
public class FeedHeaterController {

    private final FeedHeaterService feedHeaterService;

    @GetMapping
    public void heatNewsFeed() {
        feedHeaterService.heat();
    }
}
