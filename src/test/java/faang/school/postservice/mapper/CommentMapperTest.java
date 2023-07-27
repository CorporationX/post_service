package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private CommentMapperImpl commentMapper;

    private static final long COMMENT_ID = 1L;
    private static final long AUTHOR_ID = 3L;
    private static final Post POST = Post.builder().id(2L).content("Post...").build();
    private static final List<Like> LIKES = List.of(Like.builder().id(1L).build(), Like.builder().id(2L).build());

    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        comment = Comment.builder().id(COMMENT_ID).authorId(AUTHOR_ID).content("Content...").post(POST).likes(LIKES).build();
        commentDto = CommentDto.builder().id(COMMENT_ID).authorId(AUTHOR_ID).content("Content...").postId(POST.getId())
                .likesIds(LIKES.stream().map(Like::getId).toList()).build();
    }

    @Test
    public void testToDtoMethodValid() {
        CommentDto commentDtoFromMapper = commentMapper.toDto(comment);
        assertEquals(commentDto, commentDtoFromMapper);
    }

    @Test
    public void testToEntityMethodValid() {
        Mockito.when(postRepository.findById(POST.getId()))
                .thenReturn(Optional.of(POST));
        LIKES.forEach(like -> {
            Mockito.when(likeRepository.findById(like.getId()))
                    .thenReturn(Optional.of(like));
        });

        Comment commentFromMapper = commentMapper.toEntity(commentDto);
        commentMapper.convertDependenciesToEntity(commentDto, commentFromMapper);

        assertEquals(comment, commentFromMapper);
    }

    @Test
    public void testUpdateMethodValid() {
        Mockito.when(postRepository.findById(POST.getId()))
                .thenReturn(Optional.of(POST));
        LIKES.forEach(like -> {
            Mockito.when(likeRepository.findById(like.getId()))
                    .thenReturn(Optional.of(like));
        });
        Comment commentToUpdate = Comment.builder().id(11L).authorId(11L).post(new Post()).content("Other...").build();

        commentMapper.update(commentDto, commentToUpdate);
        commentMapper.convertDependenciesToEntity(commentDto, commentToUpdate);

        assertEquals(comment, commentToUpdate);
    }

    @Test
    public void testConvertMethodInvalidPost() {
        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentMapper.convertDependenciesToEntity(commentDto, comment));
        assertEquals("Post with id " + POST.getId() + " was not found!", exception.getMessage());
    }

    @Test
    public void testConvertMethodInvalidLikes() {
        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(POST));
        Mockito.when(likeRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentMapper.convertDependenciesToEntity(commentDto, comment));
        assertEquals("Like with id " + LIKES.get(0).getId() + " was not found!", exception.getMessage());
    }
}