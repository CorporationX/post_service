package faang.school.postservice;

import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;

import java.time.LocalDateTime;
import java.util.List;

public class LikeTestConstants {
    public static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now();

    public static final long USER_ID = 1L;
    public static final long POST_ID = 2L;
    public static final long LIKE_ID = 3L;
    public static final long COMMENT_ID = 4L;

    public static final UserDto USER_DTO = new UserDto(USER_ID, "User", "user@example.com");

    public static final Post POST = new Post(POST_ID, "Xoxoxo", USER_ID, null, null,
            null, null, null, null, true, LOCAL_DATE_TIME_NOW, null, false,
            LOCAL_DATE_TIME_NOW, LOCAL_DATE_TIME_NOW);

    public static final Comment COMMENT = new Comment(COMMENT_ID, "Cool!", USER_ID, null, POST,
            LOCAL_DATE_TIME_NOW, LOCAL_DATE_TIME_NOW, null, null);

    public static final LikeRequestDto LIKE_REQUEST_DTO = new LikeRequestDto(USER_ID);

    public static final Like LIKE_POST = new Like(LIKE_ID, USER_ID, null, POST, LOCAL_DATE_TIME_NOW);

    public static final Like LIKE_COMMENT = new Like(LIKE_ID, USER_ID, COMMENT, null, LOCAL_DATE_TIME_NOW);

    public static final LikeResponseDto LIKE_POST_RESPONSE_DTO = new LikeResponseDto(LIKE_ID, USER_ID, null,
            POST_ID, LOCAL_DATE_TIME_NOW);

    public static final LikeResponseDto LIKE_COMMENT_RESPONSE_DTO = new LikeResponseDto(LIKE_ID, USER_ID, COMMENT_ID,
            null, LOCAL_DATE_TIME_NOW);

    public static final long USER_ID_1 = 1L;
    public static final long USER_ID_2 = 2L;
    public static final long USER_ID_3 = 3L;
    public static final long USER_ID_4 = 4L;
    public static final long USER_ID_5 = 5L;
    public static final long USER_ID_6 = 6L;

    public static final UserDto USER_1 = new UserDto(USER_ID_1, "JohnDoe", "johndoe@example.com");
    public static final UserDto USER_2 = new UserDto(USER_ID_2, "MichaelJohnson", "michaeljohnson@example.com");
    public static final UserDto USER_3 = new UserDto(USER_ID_3, "EmilyDavis", "emilydavis@example.com");
    public static final UserDto USER_4 = new UserDto(USER_ID_4, "WilliamTaylor", "williamtaylor@example.com");
    public static final UserDto USER_5 = new UserDto(USER_ID_5, "OliviaAnderson", "oliviaanderson@example.com");
    public static final UserDto USER_6 = new UserDto(USER_ID_6, "JamesWilson", "jameswilson@example.com");

    public static final List<Long> USERS_IDS_WHO_LIKED_THE_POST
            = List.of(USER_ID_1, USER_ID_2, USER_ID_3, USER_ID_4, USER_ID_5, USER_ID_6);

    public static final List<Long> USERS_IDS_WHO_LIKED_THE_COMMENT
            = List.of(USER_ID_1, USER_ID_2, USER_ID_3, USER_ID_4, USER_ID_5, USER_ID_6);

    public static final List<UserDto> USERS_WHO_LIKED_THE_POST
            = List.of(USER_1, USER_2, USER_3, USER_4, USER_5, USER_6);

    public static final List<UserDto> USERS_WHO_LIKED_THE_COMMENT
            = List.of(USER_1, USER_2, USER_3, USER_4, USER_5, USER_6);
}
