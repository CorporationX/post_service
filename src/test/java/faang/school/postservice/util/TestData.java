package faang.school.postservice.util;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestData {
    public PostDto returnPostDto() {
        return PostDto.builder()
                .id(2L)
                .authorId(1L)
                .content("content")
                .build();
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

    public Post returnPost() {
        return Post.builder()
                .authorId(1L)
                .content("content A")
                .createdAt(LocalDateTime.of(2024, 12,12,12, 12))
                .build();
    }

    public List<Post> getDraftsOfUser() {
        Post postA = returnPost();
        Post postB = Post.builder()
                .authorId(1L)
                .content("content B")
                .createdAt(LocalDateTime.of(2024, 12,13,12, 12))
                .build();

        return List.of(postA, postB);
    }

    public List<Post> getDraftsOfProject() {
        Post postA = returnPost();
        Post postB = Post.builder()
                .projectId(1L)
                .content("content B")
                .createdAt(LocalDateTime.of(2024, 12,13,12, 12))
                .build();

        return List.of(postA, postB);
    }

    public List<Post> getPostsOfUser() {
        Post postA = returnPost();
        postA.setPublished(true);
        Post postB = Post.builder()
                .authorId(1L)
                .content("content B")
                .published(true)
                .createdAt(LocalDateTime.of(2024, 12,13,12, 12))
                .build();

        return List.of(postA, postB);
    }

    public List<Post> getPostsOfProject() {
        Post postA = returnPost();
        postA.setPublished(true);
        Post postB = Post.builder()
                .projectId(1L)
                .content("content B")
                .published(true)
                .createdAt(LocalDateTime.of(2024, 12,13,12, 12))
                .build();

        return List.of(postA, postB);
    }
}
