package faang.school.postservice.scheduler.post;

/*
 Это простой класс, чтобы настроить регулярный запуск службы в scheduledAt
 */
public interface ScheduledPostPublisher {
    void publishScheduledPosts();
}
