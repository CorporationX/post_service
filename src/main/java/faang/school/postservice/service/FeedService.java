package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostOutputDto;

import java.util.List;

public interface FeedService {
    List<PostOutputDto> getFeed(Integer offset);

    void addPostToUser(long postId, long userId);
}
