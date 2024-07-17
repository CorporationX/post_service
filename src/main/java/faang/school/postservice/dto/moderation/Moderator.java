package faang.school.postservice.dto.moderation;

public interface Moderator<T> {
    boolean inspect(T moderationObject);
}