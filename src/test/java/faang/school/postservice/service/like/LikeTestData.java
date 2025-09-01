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

    private Like like = new Like();
    private UserDto userDto = new UserDto(userId, userName, userEmail);

    private List<UserDto> userDtoList = List.of(userDto);
    private List<Like> likesList = List.of(like);
    private List<Long> userIdList = List.of(userId);

    public static LikeTestData createData() {
        return new LikeTestData();
    }

    public Like getLike() {
        return like;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public List<UserDto> getUserDtoList() {
        return userDtoList;
    }

    public List<Like> getLikesList() {
        return likesList;
    }

    public List<Long> getUserIdList() {
        return userIdList;
    }
}
