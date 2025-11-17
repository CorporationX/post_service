package faang.school.postservice.service.user;

import java.util.List;

public interface UserService {
    List<Long> getNotBannedUsersIds(List<Long> ids);
}