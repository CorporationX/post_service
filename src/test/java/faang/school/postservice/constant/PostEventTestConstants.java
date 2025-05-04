package faang.school.postservice.constant;

import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.model.Post;

import java.time.LocalDateTime;
import java.util.List;

public class PostEventTestConstants {
    public static final long POST_AUTHOR_ID = 1L;
    public static final List<Long> POST_AUTHOR_FOLLOWERS_IDS_LIST = List.of(2L, 3L, 4L, 5L);

    public static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now();

    public static final long POST_ID = 6L;

    public static final Post POST = Post.builder().content("Xoxoxo").authorId(POST_AUTHOR_ID).build();

    public static final PostEventDto POST_EVENT_DTO
            = new PostEventDto(POST_ID, LOCAL_DATE_TIME_NOW, POST_AUTHOR_FOLLOWERS_IDS_LIST);
}
