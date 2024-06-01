package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    @InjectMocks
    private LikeController likeController;

    @Mock
    private LikeValidator likeValidator;

    @Mock
    private LikeService likeService;

    private LikeDto likeDto;
    private Long id;

    @BeforeEach
    public void setUp() {
        likeDto = new LikeDto();
        id = 1L;
    }

    @Test
    public void testAddLikePost_isRunAddLikePost() {
        likeController.addLikePost(id, likeDto);
        verify(likeService, times(1)).addLikePost(id, likeDto);
    }

    @Test
    public void testAddLikeComment_isRunAddLikeComment() {
        likeController.addLikeComment(id, likeDto);
        verify(likeService, times(1)).addLikeComment(id, likeDto);
    }

    @Test
    public void testDeleteLikePost_isRunDeleteLikePost() {
        likeController.deleteLikePost(id, likeDto);
        verify(likeService, times(1)).deleteLikePost(id, likeDto);
    }

    @Test
    public void testDeleteLikeComment_isRunDeleteLikeComment() {
        likeController.deleteLikeComment(id, likeDto);
        verify(likeService, times(1)).deleteLikeComment(id, likeDto);
    }
}