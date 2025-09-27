package faang.school.postservice.service.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import java.util.ArrayList;
import java.util.List;

public class LikeTestData {
    public static final long POST_ID = 1L;
    public static final long COMMENT_ID = 2L;
    public static final long USER_ID = 3L;
    public static final String USERNAME = "User";
    public static final String EMAIL = "@123";

    public static UserDto userDto = new UserDto(USER_ID, USERNAME, EMAIL, new ArrayList<>());

    public static List<UserDto> getUserDtoList() {
        return List.of(userDto);
    }

    public static List<Like> getLikesList() {
        Like like = new Like();
        like.setUserId(USER_ID);
        return List.of(like);
    }

    public static List<Long> getUserIdList() {
        return List.of(USER_ID);
    }
}
