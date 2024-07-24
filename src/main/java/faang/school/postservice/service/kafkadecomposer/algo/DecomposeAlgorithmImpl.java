package faang.school.postservice.service.kafkadecomposer.algo;

import faang.school.postservice.publisher.kafka.KafkaPostPublisher;
import faang.school.postservice.threadpool.ThreadPoolForNewsFeedAlgo;

class DecomposeAlgorithmImpl extends DecomposeAlgorithm {
    public DecomposeAlgorithmImpl(KafkaPostPublisher kafkaPostPublisher, ThreadPoolForNewsFeedAlgo threadPoolForNewsFeedAlgo) {
        super(kafkaPostPublisher, threadPoolForNewsFeedAlgo);
    }
}
