package faang.school.postservice.service;

public interface SpellCheckService {

    String autoCorrect(String content, String language);

    String detectLanguage(String text);
}
