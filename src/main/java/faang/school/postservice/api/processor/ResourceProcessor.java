package faang.school.postservice.api.processor;

public interface ResourceProcessor<R> {
    R process(R resource);

    default boolean canBeProcessed(String mimeType, R resource) {
        return true;
    }
}
