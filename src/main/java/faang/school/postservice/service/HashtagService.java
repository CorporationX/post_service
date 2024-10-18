package faang.school.postservice.service;

import faang.school.postservice.model.dto.post.PostDto;
import faang.school.postservice.model.entity.Hashtag;
import faang.school.postservice.model.entity.Post;

import java.util.List;

public interface HashtagService {
    void createHashtags(Post post);

    List<PostDto> findPostsByHashtag(String hashtag);

    void updateHashtags(Post post);

    List<Hashtag> processHashtags(Post post);
}
