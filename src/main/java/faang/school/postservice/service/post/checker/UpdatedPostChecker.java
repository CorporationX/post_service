package faang.school.postservice.service.post.checker;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

public interface UpdatedPostChecker {

    void check(Post post, Post prevPost);
}
