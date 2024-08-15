package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PostMapperTest {

    @InjectMocks
    private final PostMapperImpl postMapper = new PostMapperImpl();

    @Spy
    private CommentMapper commentMapper = new CommentMapperImpl();

    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .content("Some content")
                .authorId(1L)
                .projectId(1L)
                .comments(List.of(Comment.builder()
                        .id(1L)
                        .content("Content")
                        .likes(List.of(Like.builder()
                                .id(1L)
                                .build()))
                        .build()))
                .build();

        postDto = PostDto.builder()
                .id(1L)
                .content("Some content")
                .authorId(1L)
                .projectId(1L)
                .comments(List.of(CommentDto.builder()
                        .id(1L)
                        .content("Content")
                        .likesId(List.of(1L))
                        .build()))
                .build();
    }

    @Test
    void testToEntity() {
        Post mappedPost = postMapper.toEntity(postDto);

        assertEquals(post.getId(), mappedPost.getId());
        assertEquals(post.getContent(), mappedPost.getContent());
        assertEquals(post.getAuthorId(), mappedPost.getAuthorId());
        assertEquals(post.getProjectId(), mappedPost.getProjectId());
        assertEquals(post.getComments(), mappedPost.getComments());
    }

    @Test
    void testToDto() {
        PostDto mappedDto = postMapper.toDto(post);

        assertEquals(postDto.getId(), mappedDto.getId());
        assertEquals(postDto.getContent(), mappedDto.getContent());
        assertEquals(postDto.getAuthorId(), mappedDto.getAuthorId());
        assertEquals(postDto.getProjectId(), mappedDto.getProjectId());
        assertEquals(postDto.getComments(), mappedDto.getComments());
    }
}