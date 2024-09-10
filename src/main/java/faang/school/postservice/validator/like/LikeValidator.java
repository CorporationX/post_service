package faang.school.postservice.validator.like;

import faang.school.postservice.client.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LikeValidator {

    private final UserServiceClient userServiceClient;

    @Autowired
    public LikeValidator(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }


    public void validateUser(Long id) {
        if (userServiceClient.getUser(id) == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
    }

}
