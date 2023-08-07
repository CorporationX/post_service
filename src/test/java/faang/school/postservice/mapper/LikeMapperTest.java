package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LikeMapperTest {

    private LikeMapper likeMapper = new LikeMapperImpl();
    LikeDto likeDto;
    Like like;

    @BeforeEach
    void setUp(){
        Comment comment = Comment.builder().id(1L).build();
        Post post = Post.builder().id(1L).build();
        likeDto = LikeDto.builder().id(1L).postId(1L).commentId(1L).userId(1L).build();
        like = Like.builder().id(1L).userId(1L).comment(comment).post(post).build();
    }

    @Test
    void toDto() {
        assertEquals(likeDto,likeMapper.toDto(like));
    }

    @Test
    void toModel() {
        like = Like.builder().id(1L).userId(1L).build();
        assertEquals(like,likeMapper.toModel(likeDto));
    }
}