package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HashtagService {
    void createHashtags(Post post);

    List<PostDto> findPostsByHashtag(String hashtag);

    void updateHashtags(Post post);

    List<Hashtag> processHashtags(Post post);
}
