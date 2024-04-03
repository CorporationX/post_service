package faang.school.postservice.hhzuserban.publisher;

import faang.school.postservice.hhzuserban.dto.message.UserBanMessage;

public interface MessagePublisher {
    void publish(UserBanMessage userBanMessage);
}
