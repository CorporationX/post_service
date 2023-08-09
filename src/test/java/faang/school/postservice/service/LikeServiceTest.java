package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @InjectMocks
    LikeService likeService;
    @Mock
    LikeDto like;
    @Mock
    LikeRepository likeRepository;
    @Mock
    LikeMapper likeMapper;
    @Mock
    PostRepository postRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserServiceClient userServiceClient;

    @BeforeEach
    public void setup(){
        Post somePost = Post.builder()
                .id(11)
                .build();
    }

    @Test
    void addLikeToPost() {


        verify(likeMapper ,times(1)).toEntity(like);



    }

    @Test
    void addLikeToComment() {
        LikeDto someLike = LikeDto.builder()
                .id(11)
                .build();

        Assertions.assertThrows(DataValidationException.class,
                () -> likeService.addLikeToPost(-11L, someLike));
    }

    @Test
    void deleteLikeFromPost() {
    }

    @Test
    void deleteLikeFromComment() {
    }
}