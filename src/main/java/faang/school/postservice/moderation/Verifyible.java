package faang.school.postservice.moderation;

public interface Verifyible {

    void setVerificationValue(boolean result);

    void initVerifiedDate();

    String getContentText();
}
