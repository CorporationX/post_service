package faang.school.postservice.client;

import faang.school.postservice.model.text.CorrectionResponse;

public interface TextCorrectionClient {
    public CorrectionResponse callCorrectionApi(String params);
}
