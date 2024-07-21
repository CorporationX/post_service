package faang.school.postservice.comment;

import faang.school.postservice.dto.comment.CommentDto;

import java.util.Objects;

import org.junit.jupiter.api.Test;

public class SomeTest {

    @Test
    public void test() {
        CommentDto commentDto  = CommentDto.builder()
                .id(10L)
                .content("дизлайк")
                .authorId(0L)
                .postId(1L)
                .build();
        CommentDto currentCommentDto = CommentDto.builder()
                .id(10L)
                .content("лайк")
                .authorId(0L)
                .postId(1L)
                .build();
        //2024-07-21 14:43:06.905 +0300	2024-07-21 14:43:06.905 +0300
        equalUpdateComment(commentDto, currentCommentDto);
    }

    private void equalUpdateComment(CommentDto commentDto, CommentDto currentCommentDto) {
        if (!Objects.equals(commentDto.getAuthorId(), currentCommentDto.getAuthorId())
                || !Objects.equals(commentDto.getLikeIds(), currentCommentDto.getLikeIds())
                || !Objects.equals(commentDto.getPostId(), currentCommentDto.getPostId())
            //|| !Objects.equals(commentDto.getCreatedAt(), currentCommentDto.getCreatedAt())
            //|| !Objects.equals(commentDto.getUpdatedAt(), currentCommentDto.getUpdatedAt())
        ) {
            throw new IllegalArgumentException("ERRORS");
        }
    }
}
