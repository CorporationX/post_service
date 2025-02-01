package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("feeds")
public class FeedControllerImpl implements FeedController {

    private final FeedService feedService;

    @PostMapping("heat")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<FeedDto> heatHeed() {
        return feedService.heat();
    }
}
