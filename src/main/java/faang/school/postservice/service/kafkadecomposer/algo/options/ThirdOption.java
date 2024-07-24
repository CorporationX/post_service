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
@Order(3)
public class ThirdOption extends DecomposeAlgorithm implements AlgoOptions {

    @Value("${value.algoDecompose.thirdOption}")
    private int thirdOption;
    @Value("${value.algoDecompose.thirdOptionBatch}")
    private int thirdOptionBatch;

    public ThirdOption(KafkaPostPublisher kafkaPostPublisher, ThreadPoolForNewsFeedAlgo threadPoolForNewsFeedAlgo) {
        super(kafkaPostPublisher, threadPoolForNewsFeedAlgo);
    }

    @Override
    public int limit() {
        return thirdOption;
    }

    public void decompose(List<UserFeedDto> userDtoList, Long postId) {
        startAlgo(userDtoList, postId, thirdOptionBatch);
    }
}
