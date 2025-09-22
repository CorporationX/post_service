package faang.school.postservice.producer;

import faang.school.postservice.dto.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.post-views.name}")
    private String postViewsTopicName;

    /**
     * Отправляет событие о просмотре поста в Kafka
     *
     * @param postId ID поста
     * @param viewerId ID пользователя, который просматривает пост
     * @param authorId ID автора поста
     * @param projectId ID проекта (может быть null)
     * @param ipAddress IP адрес пользователя
     * @param userAgent User Agent браузера
     * @param source источник просмотра
     * @param sessionId ID сессии пользователя
     */
    public void sendPostViewEvent(Long postId, Long viewerId, Long authorId, Long projectId,
                                  String ipAddress, String userAgent, String source, String sessionId) {
        try {
            log.debug("Preparing to send post view event for post ID: {}, viewer ID: {}", postId, viewerId);

            PostViewEvent event = PostViewEvent.builder()
                    .postId(postId)
                    .viewerId(viewerId)
                    .authorId(authorId)
                    .projectId(projectId)
                    .viewedAt(LocalDateTime.now())
                    .ipAddress(sanitizeIpAddress(ipAddress))
                    .userAgent(sanitizeUserAgent(userAgent))
                    .source(source != null ? source : "unknown")
                    .sessionId(sessionId)
                    .eventTimestamp(LocalDateTime.now())
                    .build();

            String partitionKey = String.valueOf(postId);

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(postViewsTopicName, partitionKey, event);

            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    handleSendFailure(postId, viewerId, event, throwable);
                } else {
                    handleSendSuccess(postId, viewerId, result);
                }
            });

        } catch (Exception e) {
            log.error("Failed to prepare post view event for post ID: {}, viewer ID: {}",
                    postId, viewerId, e);
        }
    }

    public void sendPostViewEvent(Long postId, Long viewerId, Long authorId, Long projectId,
                                  String ipAddress, String userAgent, String source,
                                  String sessionId, Long viewDurationMs) {
        try {
            log.debug("Preparing to send post view event with duration for post ID: {}, viewer ID: {}, duration: {}ms",
                    postId, viewerId, viewDurationMs);

            PostViewEvent event = PostViewEvent.builder()
                    .postId(postId)
                    .viewerId(viewerId)
                    .authorId(authorId)
                    .projectId(projectId)
                    .viewedAt(LocalDateTime.now())
                    .ipAddress(sanitizeIpAddress(ipAddress))
                    .userAgent(sanitizeUserAgent(userAgent))
                    .source(source != null ? source : "unknown")
                    .sessionId(sessionId)
                    .viewDurationMs(viewDurationMs)
                    .eventTimestamp(LocalDateTime.now())
                    .build();

            String partitionKey = String.valueOf(postId);
            kafkaTemplate.send(postViewsTopicName, partitionKey, event);

        } catch (Exception e) {
            log.error("Failed to send post view event with duration for post ID: {}, viewer ID: {}",
                    postId, viewerId, e);
        }
    }

    public void sendPostViewEvent(Long postId, Long viewerId, Long authorId) {
        sendPostViewEvent(postId, viewerId, authorId, null, null, null, "api", null);
    }

    private void handleSendSuccess(Long postId, Long viewerId, SendResult<String, Object> result) {
        log.debug("Successfully sent post view event for post ID: {}, viewer ID: {} to partition: {}, offset: {}",
                postId, viewerId,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
    }

    private void handleSendFailure(Long postId, Long viewerId, PostViewEvent event, Throwable throwable) {
        log.error("Failed to send post view event for post ID: {}, viewer ID: {} to Kafka topic: {}",
                postId, viewerId, postViewsTopicName, throwable);
    }

    private String sanitizeIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return null;
        }

        if (ipAddress.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            String[] parts = ipAddress.split("\\.");
            if (parts.length == 4) {
                return parts[0] + "." + parts[1] + "." + parts[2] + ".xxx";
            }
        }
        return ipAddress.length() > 45 ? null : ipAddress;
    }

    private String sanitizeUserAgent(String userAgent) {
        if (userAgent == null) {
            return null;
        }

        String sanitized = userAgent.length() > 500 ? userAgent.substring(0, 500) : userAgent;

        sanitized = sanitized.replaceAll("[\\r\\n\\t]", " ");

        return sanitized;
    }

    public void sendPostViewEvents(java.util.List<PostViewEvent> viewEvents) {
        for (PostViewEvent event : viewEvents) {
            try {
                String partitionKey = String.valueOf(event.getPostId());
                kafkaTemplate.send(postViewsTopicName, partitionKey, event);
            } catch (Exception e) {
                log.error("Failed to send batch post view event for post ID: {}",
                        event.getPostId(), e);
            }
        }
        log.info("Sent batch of {} post view events", viewEvents.size());
    }
}