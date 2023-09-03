package faang.school.postservice.validation;

import faang.school.postservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class LikeControllerValidator {

    public void validate(long id){
        if (id <= 0){
            throw new DataValidationException("Id cannot be less than 1 !");
        }
    }

    public void validateTwoIds(long id1, long id2){
        if(id1 <= 0 || id2 <= 0){
            throw new DataValidationException("Id cannot be less than 1 !");
        }
    }
}
