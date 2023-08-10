package faang.school.postservice.validation;

import org.springframework.stereotype.Component;

@Component
public class LikeControllerValidator {

    public void validate(long id){
        if (id <= 0){
            throw new IllegalArgumentException("Id cannot be less than 1 !");
        }
    }
    public void validate(long Id, long anotherId){
        if(Id <= 0 || anotherId <= 0){
            throw new IllegalArgumentException("Id cannot be less than 1 !");
        }
    }
}
