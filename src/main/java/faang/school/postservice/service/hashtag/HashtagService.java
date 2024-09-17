package faang.school.postservice.service.hashtag;

import faang.school.postservice.model.Post;

import java.util.List;

public interface HashtagService {
    void createHashtags(Post post);
    List<Post> findPostsByHashtag(String hashtag);
}
