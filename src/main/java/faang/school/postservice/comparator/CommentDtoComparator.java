package faang.school.postservice.comparator;

import faang.school.postservice.dto.comment.CommentFeedDto;

import java.util.Comparator;

public class CommentDtoComparator implements Comparator<CommentFeedDto> {

    @Override
    public int compare(CommentFeedDto firstDto, CommentFeedDto secondDto) {
        return firstDto.getCreatedAt().compareTo(secondDto.getCreatedAt());
    }
}
