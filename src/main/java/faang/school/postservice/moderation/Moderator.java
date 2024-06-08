package faang.school.postservice.moderation;

public interface Moderator<T> {
    boolean inspect(T moderationObject);
}