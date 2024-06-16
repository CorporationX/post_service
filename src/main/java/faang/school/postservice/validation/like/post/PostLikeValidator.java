package faang.school.postservice.validation.like.post;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validation.like.SimultaneousLikeValidator;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PostLikeValidator {
    private final UserServiceClient userClient;
    private final LikeRepository likeRepository;
    //TODO: Узнать у Сабины подходит ли это под паттерн Стратегия
    private SimultaneousLikeValidator simultaneousLikeValidator;
    
    @Autowired
    public void setSimultaneousLikeValidator(
        @Qualifier("simultaneousPostLikeValidator") SimultaneousLikeValidator simultaneousLikeValidator
    ) {
        this.simultaneousLikeValidator = simultaneousLikeValidator;
    }
    
    public void verifyCanLikePost(LikeDto dto) {
        verifyUserExist(dto);
        
        simultaneousLikeValidator.verifyNotExists(dto);
    }
    
    public void verifyLikeExists(Long likeId) {
        likeRepository.findById(likeId).orElseThrow(
            ()-> new NotFoundException(String.format("Like with id %d not found", likeId))
        );
    }
    
    private void verifyUserExist(LikeDto dto) {
        Long userId = dto.getUserId();
        Optional.ofNullable(userClient.getUser(userId)).orElseThrow(
            () -> new NotFoundException(String.format("User with id %d not found", userId))
        );
    }
}
