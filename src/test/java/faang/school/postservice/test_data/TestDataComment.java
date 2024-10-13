package faang.school.postservice.test_data;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
public class TestDataComment {
    public UserDto getUserDto() {
        return UserDto.builder()
                .id(1L)
                .username("User1")
                .email("User1@mail.com")
                .build();
    }

    public UserDto getUserDto2() {
        return UserDto.builder()
                .id(2L)
                .username("User2")
                .email("User2@mail.com")
                .build();
    }

    public ProjectDto getProjectDto() {
        return ProjectDto.builder()
                .id(1L)
                .title("testProjectTitle")
                .build();
    }

    public Post getPost() {
        return Post.builder()
                .id(1L)
                .content("testPostContent")
                .authorId(getUserDto().getId())
                .projectId(getProjectDto().getId())
                .comments(new ArrayList<>())
                .published(true)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Comment getComment1() {
        return Comment.builder()
                .id(1L)
                .content("testCommentContent1")
                .authorId(getUserDto().getId())
                .post(getPost())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Comment getComment2() {
        return Comment.builder()
                .id(2L)
                .content("testCommentContent2")
                .authorId(getUserDto().getId())
                .post(getPost())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public CommentDto getCommentDto1() {
        return CommentDto.builder()
                .id(1L)
                .content("testCommentContent1")
                .authorId(getUserDto().getId())
                .postId(getPost().getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public CommentUpdateDto getCommentUpdateDto() {
        return CommentUpdateDto.builder()
                .id(1L)
                .content("testCommentContent1")
                .authorId(getUserDto().getId())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public CommentEventDto getCommentEventDto(){
        return CommentEventDto.builder()
                .commentId(1L)
                .commentAuthorId(1L)
                .postId(1L)
                .postAuthorId(1L)
                .commentContent("testCommentContent1")
                .build();
    }
}
