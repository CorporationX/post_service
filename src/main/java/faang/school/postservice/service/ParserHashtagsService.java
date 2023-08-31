package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;

public interface ParserHashtagsService {
    void send(PostDto postDto, String message);
    String getHashtags();
}
