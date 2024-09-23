package faang.school.postservice.exception.messages;

public enum FeedExceptionMessage {

    BATCH_SIZE_TOO_LARGE(
            "The requested number of posts is more than the allowed value"
    );

    private final String messageTemplate;

    FeedExceptionMessage(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

}
