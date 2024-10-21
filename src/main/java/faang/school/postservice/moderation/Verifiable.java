package faang.school.postservice.moderation;

import java.time.LocalDateTime;

public interface Verifiable {

    void setVerified(Boolean verified);

    void setVerifiedDate(LocalDateTime verifiedDate);

    String getContent();
}
