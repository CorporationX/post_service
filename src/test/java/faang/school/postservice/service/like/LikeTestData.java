package faang.school.postservice.service.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class LikeTestData {
    public static long postId = 1L;
    public static long commentId = 2L;
    public static long userId = 3L;
    public static String userName = "User";
    public static String userEmail = "@123";

    private static final Like like = getLike();
    private static final UserDto userDto = new UserDto(userId, userName, userEmail);

    public static Like getLike() {
        Like like = new Like();
        like.setUserId(userId);
        return like;
    }
    public UserDto getUserDto() {
        return userDto;
    }

    public static List<UserDto> getUserDtoList() {
        return List.of(userDto);
    }

    public static List<Like> getLikesList() {
        return List.of(like);
    }

    public static List<Long> getUserIdList() {
        return List.of(userId);
    }
}
