package faang.school.postservice.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UserFollowersDto {
    private long userId;
    private List<Long> followersIds;


}
