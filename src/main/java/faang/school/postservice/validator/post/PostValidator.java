package faang.school.postservice.validator.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.post.PostWOAuthorException;
import faang.school.postservice.exception.post.ImmutablePostDataException;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.post.PostWithTwoAuthorsException;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PostValidator {

    public void validateAuthor(PostDto postDto) {
        boolean flagFirst = postDto.getAuthorId() == null;
        boolean flagSecond = postDto.getProjectId() == null;
        if (flagFirst && flagSecond) {
            throw new PostWOAuthorException();
        }
        if (flagFirst == flagSecond) {
            throw new PostWithTwoAuthorsException();
        }
    }

    public void validatePublished(Post post) {
        if (post.isPublished()) {
            throw new PostAlreadyPublishedException();
        }
    }

    public void checkImmutableData(PostDto postDto, Post post) {
        validateAuthor(postDto);
        if (!Objects.equals(postDto.getAuthorId(), post.getAuthorId())
                || !Objects.equals(postDto.getProjectId(), post.getProjectId())) {
            throw new ImmutablePostDataException("Автора нельзя изменить.");
        }
    }
}
