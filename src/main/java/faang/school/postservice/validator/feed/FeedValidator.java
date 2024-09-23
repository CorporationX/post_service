package faang.school.postservice.validator.feed;

import faang.school.postservice.exception.feed.BatchSizeIsLargerThanMaxFeedSizeException;
import faang.school.postservice.exception.messages.FeedExceptionMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeedValidator {

    @Value("${feed.max-size}")
    private int maxFeedSize;

    public void validateMaxFeedSize(int batchSize) {
        if(batchSize > maxFeedSize) {
            throw new BatchSizeIsLargerThanMaxFeedSizeException(FeedExceptionMessage.BATCH_SIZE_TOO_LARGE);
        }
    }
}
