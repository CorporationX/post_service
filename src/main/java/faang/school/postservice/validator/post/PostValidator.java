package faang.school.postservice.validator.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.post.PostValidatorException;
import faang.school.postservice.model.Post;

import java.util.Objects;

public class PostValidator {

    public void validateAuthor(PostDto postDto) {
        boolean flagFirst = postDto.getAuthorId() == null;
        boolean flagSecond = postDto.getProjectId() == null;
        if (flagFirst && flagSecond) {
            throw new PostValidatorException("У статьи отсутствует автор.");
        }
        if (flagFirst == flagSecond) {
            throw new PostValidatorException("Статья не может иметь двух авторов.");
        }
    }

    public void validatePublished(Post post) {
        if (!post.isPublished()) {
            throw new PostValidatorException("Сообщение уже опубликовано.");
        }
    }

    public void checkImmutableData(PostDto postDto, Post post) {
        validateAuthor(postDto);
        if (!Objects.equals(postDto.getAuthorId(), post.getAuthorId())
                || !Objects.equals(postDto.getProjectId(), post.getProjectId())) {
            throw new PostValidatorException("Автора нельзя изменить.");
        }
    }
}
