package faang.school.postservice.dto.post;

import faang.school.postservice.comparator.CommentDtoComparator;
import faang.school.postservice.dto.comment.CommentFeedDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.TreeSet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostFeedDto {

    private Long id;

    private String content;

    private String authorName;

    private TreeSet<CommentFeedDto> lastComments = new TreeSet<>(new CommentDtoComparator());

    private int commentsAmount;

    private long likesAmount;

    private LocalDateTime publishedAt;

//    static class CommentDtoComparator implements Comparator<CommentFeedDto> {
//
//        @Override
//        public int compare(CommentFeedDto firstDto, CommentFeedDto secondDto) {
//            return firstDto.getCreatedAt().compareTo(secondDto.getCreatedAt());
//        }
//    }
}