package faang.school.postservice.validation;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidation {
    private final UserServiceClient userServiceClient;

    public void authorValidation(Long userId) {
        try{
            UserDto user = userServiceClient.getUser(userId);
        }catch(FeignException e) {
            throw new EntityNotFoundException("User is not found");
        }
    }

}
