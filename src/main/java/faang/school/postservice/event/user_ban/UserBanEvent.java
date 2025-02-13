package faang.school.postservice.event.user_ban;

import faang.school.postservice.event.Event;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserBanEvent implements Event {
    private long userId;
    private boolean banned;
}
