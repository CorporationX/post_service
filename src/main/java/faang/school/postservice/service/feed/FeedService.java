package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedDto;

import java.util.List;

public interface FeedService {

    List<FeedDto> heat();

    FeedDto findById(String userId);
}
