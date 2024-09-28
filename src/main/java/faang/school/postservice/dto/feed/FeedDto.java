package faang.school.postservice.dto.feed;

import faang.school.postservice.comparator.PostDtoComparator;
import faang.school.postservice.dto.post.PostFeedDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.TreeSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedDto {

    private Long userId;

    private TreeSet<PostFeedDto> posts = new TreeSet<>(new PostDtoComparator());

//    public static class PostDtoComparator implements Comparator<PostFeedDto> {
//
//        @Override
//        public int compare(PostFeedDto firstDto, PostFeedDto secondDto) {
//            return firstDto.getPublishedAt().compareTo(secondDto.getPublishedAt());
//        }
//    }
}