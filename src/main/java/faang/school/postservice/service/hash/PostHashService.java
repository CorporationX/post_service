package faang.school.postservice.service.hash;

import faang.school.postservice.dto.event.CommentEventKafka;
import faang.school.postservice.dto.event.LikeEventKafka;
import faang.school.postservice.dto.event.PostEventKafka;
import faang.school.postservice.dto.event.PostViewEventKafka;

public interface PostHashService {
    void savePost(PostEventKafka postEventKafka);

    void addComment(CommentEventKafka commentEventKafka);

    void addLikeToPost(LikeEventKafka likeEventKafka);

    void addPostView(PostViewEventKafka postViewEventKafka);
}
