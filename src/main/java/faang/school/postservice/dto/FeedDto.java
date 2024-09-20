package faang.school.postservice.dto;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class FeedDto {
    private UserDto author;
    private String postContent;
    private List<CommentDto> comments;
//    private HashMap<UserDto,List<String>> comments;
    private long likes;
}
