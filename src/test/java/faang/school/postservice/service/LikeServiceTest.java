package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataLikeValidation;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private LikeValidator likeValidator;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentService commentService;

    @Mock
    private PostService postService;

    private Long id;
    private LikeDto likeDto;


    @BeforeEach
    public void setUp() {
        id = 1L;
        likeDto = new LikeDto();
    }

    @Test
    public void testAddLikePost_notPostInBd() {
        when(postService.getPostById(id)).thenThrow(DataLikeValidation.class);
        assertThrows(DataLikeValidation.class, () -> likeService.addLikePost(id, likeDto));
    }

    @Test
    public void testAddLikeComment_notCommentInBd() {
        when(commentService.getCommentById(id)).thenThrow(DataLikeValidation.class);
        assertThrows(DataLikeValidation.class, () -> likeService.addLikeComment(id, likeDto));
    }

    @Test
    public void testDeleteLikePost_notPostInBd() {
        when(postService.getPostById(id)).thenThrow(DataLikeValidation.class);
        assertThrows(DataLikeValidation.class, () -> likeService.deleteLikePost(id, likeDto));
    }

    @Test
    public void testDeleteLikeComment_notCommentInBd() {
        when(commentService.getCommentById(id)).thenThrow(DataLikeValidation.class);
        assertThrows(DataLikeValidation.class, () -> likeService.deleteLikeComment(id, likeDto));
    }
}