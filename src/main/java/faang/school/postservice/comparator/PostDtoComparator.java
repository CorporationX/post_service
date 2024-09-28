package faang.school.postservice.comparator;

import faang.school.postservice.dto.post.PostFeedDto;

import java.util.Comparator;

public class PostDtoComparator implements Comparator<PostFeedDto> {

    @Override
    public int compare(PostFeedDto firstDto, PostFeedDto secondDto) {
        return firstDto.getPublishedAt().compareTo(secondDto.getPublishedAt());
    }
}
