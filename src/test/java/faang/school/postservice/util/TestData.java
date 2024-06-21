package faang.school.postservice.util;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestData {

    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 12, 12, 12, 12);
    public static final long AUTHOR_ID = 1L;

    public PostDto returnPostDto() {
        return PostDto.builder()
                .id(2L)
                .authorId(AUTHOR_ID)
                .content("content")
                .build();
    }

    public Comment returnComment() {
        Comment comment = new Comment();
        comment.setCreatedAt(CREATED_AT);
        comment.setAuthorId(AUTHOR_ID);
        comment.setContent("NewContent");
        return comment;
    }

    public List<Comment> returnListOfComments() {
        Comment commentNew = returnComment();
        Comment commentOld = new Comment();
        commentOld.setCreatedAt(CREATED_AT);
        List<Comment> comments = new ArrayList<>();
        comments.add(commentNew);
        comments.add(commentNew);
        return comments;
    }

    public Post returnPostCreatedByUser(long authorId,
                                        String content,
                                        LocalDateTime createdAt,
                                        boolean isPublished) {
        return Post.builder()
                .authorId(authorId)
                .content(content)
                .published(isPublished)
                .createdAt(createdAt)
                .build();
    }

    private Post returnPostCreatedByProject(long projectId,
                                            String content,
                                            LocalDateTime createdAt,
                                            boolean isPublished) {
        return Post.builder()
                .projectId(projectId)
                .published(isPublished)
                .content(content)
                .createdAt(createdAt)
                .build();
    }

    public List<Post> getDraftsOfUser() {
        Post postA = returnPostCreatedByUser(AUTHOR_ID, "content A", CREATED_AT, false);
        Post postB = returnPostCreatedByUser(AUTHOR_ID, "content B", CREATED_AT.plusDays(1), false);

        return List.of(postA, postB);
    }

    public List<Post> getDraftsOfProject() {
        Post postA = returnPostCreatedByProject(AUTHOR_ID, "content A", CREATED_AT, false);
        Post postB = returnPostCreatedByProject(AUTHOR_ID, "content B", CREATED_AT.plusDays(1), false);

        return List.of(postA, postB);
    }

    public List<Post> getPostsOfUser() {
        Post postA = returnPostCreatedByUser(AUTHOR_ID, "content A", CREATED_AT, true);
        Post postB = returnPostCreatedByUser(AUTHOR_ID, "content B", CREATED_AT.plusDays(1), true);

        return List.of(postA, postB);
    }

    public List<Post> getPostsOfProject() {
        Post postA = returnPostCreatedByProject(AUTHOR_ID, "content A", CREATED_AT, true);
        Post postB = returnPostCreatedByProject(AUTHOR_ID, "content A", CREATED_AT.plusDays(1), true);

        return List.of(postA, postB);
    }
}
