package faang.school.postservice.job.moderation;

import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PostModeratorBase implements PostModerator {

    private PostModerator next;

    public void handle(Post post) {
        if (post.isVerified()) {
            if (next != null) {
                next.handle(post);
            }
        } else {
            log.info("{}: post {} Not verified", Thread.currentThread().getName(), post.getId());
        }
    }

    @Override
    public void setNext(PostModerator next) {
        this.next = next;
    }
}
