package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;

import java.time.LocalDateTime;

public class LikeServiceTestConstants {
    protected static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now();

    protected static final long USER_ID = 1L;
    protected static final long POST_ID = 2L;
    protected static final long LIKE_ID = 3L;
    protected static final long COMMENT_ID = 4L;

    protected static final UserDto USER_DTO = new UserDto(USER_ID, "User", "user@example.com");

    protected static final Post POST = new Post(POST_ID, "Xoxoxo", USER_ID, null, null,
            null, null, null, null, true, LOCAL_DATE_TIME_NOW, null, false,
            LOCAL_DATE_TIME_NOW, LOCAL_DATE_TIME_NOW);

    protected static final Comment COMMENT = new Comment(COMMENT_ID, "Cool!", USER_ID, null, POST,
            LOCAL_DATE_TIME_NOW, LOCAL_DATE_TIME_NOW, null, null);

    protected static final LikeRequestDto LIKE_REQUEST_DTO = new LikeRequestDto(USER_ID);

    protected static final Like LIKE_POST = new Like(LIKE_ID, USER_ID, null, POST, LOCAL_DATE_TIME_NOW);

    protected static final Like LIKE_COMMENT = new Like(LIKE_ID, USER_ID, COMMENT, null, LOCAL_DATE_TIME_NOW);

    protected static final LikeResponseDto LIKE_POST_RESPONSE_DTO = new LikeResponseDto(LIKE_ID, USER_ID, null,
            POST_ID, LOCAL_DATE_TIME_NOW);

    protected static final LikeResponseDto LIKE_COMMENT_RESPONSE_DTO = new LikeResponseDto(LIKE_ID, USER_ID, COMMENT_ID,
            null, LOCAL_DATE_TIME_NOW);
}
