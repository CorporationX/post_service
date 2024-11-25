package faang.school.postservice.controller;

import faang.school.postservice.service.feedheater.FeedHeater;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/v1/")
@RequiredArgsConstructor
public class FeedHeaterController {
    private final FeedHeater feedHeater;

    @PutMapping("heat")
    public void heat() {
        feedHeater.heat();
    }
}
