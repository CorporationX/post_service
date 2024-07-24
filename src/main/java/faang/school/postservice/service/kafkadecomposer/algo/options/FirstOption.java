package faang.school.postservice.service.kafkadecomposer.algo.options;

import faang.school.postservice.dto.user.UserFeedDto;
import faang.school.postservice.publisher.kafka.KafkaPostPublisher;
import faang.school.postservice.service.kafkadecomposer.algo.DecomposeAlgorithm;
import faang.school.postservice.threadpool.ThreadPoolForNewsFeedAlgo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Order(1)
public class FirstOption extends DecomposeAlgorithm implements AlgoOptions  {

    @Value("${value.algoDecompose.firstOption}")
    private int firstOption;
    @Value("${value.algoDecompose.firstOptionBatch}")
    private int firstOptionBatch;

    public FirstOption(KafkaPostPublisher kafkaPostPublisher, ThreadPoolForNewsFeedAlgo threadPoolForNewsFeedAlgo) {
        super(kafkaPostPublisher, threadPoolForNewsFeedAlgo);
    }

    @Override
    public int limit() {
        return firstOption;
    }

    public void decompose(List<UserFeedDto> userDtoList, Long postId) {
        startAlgo(userDtoList, postId, firstOptionBatch);
    }
}
