package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedController {

	private final FeedService feedService;

	@Value("${feed.limit-posts:20}")
	private int limitPosts;

	@GetMapping("/feed")
	public List<FeedPostDto> getFeed(@RequestParam(required = false) Long afterPostId) {
		return feedService.getFeed(afterPostId, limitPosts);
	}
}