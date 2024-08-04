package faang.school.postservice.job.moderation;

import faang.school.postservice.model.Post;

public interface PostModerator {

    void setNext(PostModerator next);

    void handle(Post post);
}
