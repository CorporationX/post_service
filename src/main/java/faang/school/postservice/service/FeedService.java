package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;

import java.util.Set;

public interface FeedService {
    Set<PostDto> getFeed(Long userId, Long postId);
}
