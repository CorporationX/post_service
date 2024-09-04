package faang.school.postservice.service.post;


import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.publishable.PostEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    @Transactional
    public PostDto publishPost(long postId) {
        // publishPost logic
        long authorId = postId;

        PostEvent postEvent = new PostEvent(authorId, postId);
        return new PostDto();
    }
}
