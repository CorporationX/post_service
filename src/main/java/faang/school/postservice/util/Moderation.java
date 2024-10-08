package faang.school.postservice.util;

import faang.school.postservice.model.Post;

public interface Moderation {
    boolean isVerified(Post post);
}
