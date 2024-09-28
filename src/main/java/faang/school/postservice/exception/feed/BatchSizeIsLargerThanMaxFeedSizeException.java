package faang.school.postservice.exception.feed;

import faang.school.postservice.exception.messages.FeedExceptionMessage;

public class BatchSizeIsLargerThanMaxFeedSizeException extends RuntimeException{
    public BatchSizeIsLargerThanMaxFeedSizeException(FeedExceptionMessage message) {
        super(String.valueOf(message));
    }
}
