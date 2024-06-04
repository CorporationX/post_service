package faang.school.postservice.util;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Comment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestData {
    public PostDto returnPostDto() {
        PostDto postDto = new PostDto();
        postDto.setId(2L);
        return postDto;
    }

    public Comment returnComment() {
        Comment comment = new Comment();
        comment.setId(2L);
        comment.setAuthorId(1L);
        comment.setContent("NewContent");
        comment.setCreatedAt(LocalDateTime.of(2024, Month.MAY, 31, 0, 0, 0));
        return comment;
    }

    public List<Comment> returnListOfComments() {
        Comment commentNew = returnComment();
        Comment commentOld = new Comment();
        commentOld.setCreatedAt(LocalDateTime.of(2023, Month.MAY, 31, 0, 0, 0));
        List<Comment> comments = new ArrayList<>();
        comments.add(commentNew);
        comments.add(commentNew);
        return comments;
    }
}
