package faang.school.postservice.client;

import faang.school.postservice.model.text.CorrectionResponse;

public interface TextCorrectionClient {
    CorrectionResponse callCorrectionApi(String params);
}
