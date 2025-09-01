package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;

public class LikeDataController {
    public static long userId = 1L;
    public static String userName = "User";
    public static String userEmail = "@123";
    public static long postId = 2L;
    public static long commentId = 1L;

    public static UserDto getUserDto() {
        return new UserDto(userId, userName, userEmail);
    }
}
