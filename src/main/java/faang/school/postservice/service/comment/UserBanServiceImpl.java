package faang.school.postservice.service.comment;

import faang.school.postservice.messages.redis.publishers.Publisher;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserBanServiceImpl implements UserBanService {
    private final CommentRepository commentRepository;
    private final Publisher publisher;
    private final ChannelTopic userBanTopic;
    @Value("${scheduler.comment.count-ban-size}")
    private int banSize;

    @Override
    public void banUserComment() {
        List<Long> banUsersId = commentRepository.findAllUsersForBan(banSize);
        if (!banUsersId.isEmpty()) {
            publisher.publish(userBanTopic, banUsersId);
        }
    }
}