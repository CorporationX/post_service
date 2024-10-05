package faang.school.postservice.dto.spelling_corrector.yandex_speller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class YandexSpellerCorrectResponse {
    private int code;
    private int pos;
    private int len;
    private String word;
    private List<String> s;
}
