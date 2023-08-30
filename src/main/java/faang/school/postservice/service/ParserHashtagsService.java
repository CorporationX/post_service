package faang.school.postservice.service;

public interface ParserHashtagsService {
    void send(PostDto postDto, String message);
    String getHashtags();
}
