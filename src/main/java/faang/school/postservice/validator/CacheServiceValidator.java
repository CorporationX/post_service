package faang.school.postservice.validator;

import faang.school.postservice.events.LikeEvent;
import org.springframework.stereotype.Component;

@Component
public class CacheServiceValidator {
    public void validateLikeEvent(LikeEvent likeEvent){
        if(likeEvent.getPostId()<1||likeEvent.getUserId()<1){
            throw new IllegalStateException("Wrong likeEvent Values") ;
        }
    }
}
