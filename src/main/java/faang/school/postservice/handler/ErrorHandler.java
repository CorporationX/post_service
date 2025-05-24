package faang.school.postservice.handler;

@FunctionalInterface
public interface ErrorHandler {
    String handle(Exception ex);
}
