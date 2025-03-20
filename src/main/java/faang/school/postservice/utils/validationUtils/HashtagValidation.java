package faang.school.postservice.utils.validationUtils;

import faang.school.postservice.dto.hashtag.HashtagRequestDto;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HashtagValidation {
    public static final String HASHTAG_REQUEST_DTO_CANT_BE_NULL = "HashtagRequestDto can't be null";
    public static final String TAG_IN_REQUEST_DTO_CANT_BE_NULL = "Tag in request dto can't be null";
    public static final String PAGE_IN_REQUEST_DTO_CANT_BE_NEGATIVE = "Page in request dto can't be negative";
    public static final String SIZE_IN_REQUEST_DTO_CANT_BE_NEGATIVE = "Size in request dto can't be negative";

    public static void validateHashtagRequestDto(HashtagRequestDto hashtagRequestDto) {
        List<String> errors = new ArrayList<>();

        if (hashtagRequestDto == null) {
            errors.add(HASHTAG_REQUEST_DTO_CANT_BE_NULL);
        } else {
            if (hashtagRequestDto.getTag() == null) {
                errors.add(TAG_IN_REQUEST_DTO_CANT_BE_NULL);
            }
            if (hashtagRequestDto.getPage() < 0) {
                errors.add(PAGE_IN_REQUEST_DTO_CANT_BE_NEGATIVE);
            }
            if (hashtagRequestDto.getSize() < 0) {
                errors.add(SIZE_IN_REQUEST_DTO_CANT_BE_NEGATIVE);
            }
        }

        if (!errors.isEmpty()) {
            String errorMessage = String.join(", ", errors);
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}