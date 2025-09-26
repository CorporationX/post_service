package faang.school.postservice.service;

import faang.school.postservice.dto.post.TextCheckResponse;

public interface PostCorrectorService {

    TextCheckResponse checkText(String textToCheck);
}
