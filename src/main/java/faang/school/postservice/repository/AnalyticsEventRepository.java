package faang.school.postservice.repository;

import faang.school.postservice.model.analytic.AnalyticsEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.stream.Stream;

public interface AnalyticsEventRepository extends CrudRepository<AnalyticsEvent, Long> {

    Stream<AnalyticsEvent> findByAuthorIdAndReceiverIdOrderByCreatedAtDesc(Long authorId, Long receiverId);
}
