package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Spy
    LikeMapper likeMapper = new LikeMapperImpl();

    @Mock
    private LikeValidator likeValidator;

    private LikeDto likeDto;

    @Test
    void testLikePost() {
        likeDto = LikeDto.builder().userId(1L).postId(1L).build();

        Post post = Post.builder().id(1L).build();
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));

        Like like = Like.builder().id(0L).userId(1L).post(post).build();

        assertEquals(likeMapper.toDto(like), likeService.likePost(1L, likeDto));
        verify(likeRepository).save(like);
    }

    @Test
    void testUnlikePost() {
        likeService.unlikePost(1L, 1L);
        verify(likeRepository).deleteByPostIdAndUserId(1L, 1L);
    }
}