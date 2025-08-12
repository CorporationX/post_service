package faang.school.postservice.util;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Утилитный класс для работы с хэштегами.
 * <p>
 * Содержит методы для извлечения хэштегов из текстового содержимого.
 * </p>
 *
 * @author Myrza
 * @since 07.08.2025
 */

public class HashtagUtils {
    /**
     * Извлекает все хэштеги из переданного текста.
     * <p>
     * Хэштег определяется как символ {@code #}, за которым следует одна или более буквенно-цифровых символов или
     * символов подчёркивания ({@code [a-zA-Z0-9_]}).
     * </p>
     *
     * @param content текст, из которого нужно извлечь хэштеги
     * @return список найденных хэштегов в порядке их появления в тексте;
     * если хэштегов нет, возвращается пустой список
     */
    public static List<String> getHashtags(String content) {
        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(content);
        return matcher.results()
                .map(MatchResult::group)
                .toList();
    }
}
