package faang.school.postservice.util;

import faang.school.postservice.model.post.Post;

public interface Moderation {
    boolean isVerified(Post post);
}
