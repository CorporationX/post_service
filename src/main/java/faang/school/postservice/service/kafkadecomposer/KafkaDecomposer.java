package faang.school.postservice.service.kafkadecomposer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserFeedDto;
import faang.school.postservice.service.kafkadecomposer.algo.options.AlgoOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaDecomposer {
    private final UserServiceClient userServiceClient;
    private final List<AlgoOptions> algoOptions;

    @Async("newsFeedPool")
    public void decompose(Long postAuthorId, Long postId) {
        List<UserFeedDto> userDtoList = userServiceClient.getFollowingId(postAuthorId);

       algoOptions.stream().filter(option -> userDtoList.size() <= option.limit()).findFirst()
               .ifPresent(option -> option.decompose(userDtoList, postId));
    }
}
