package faang.school.postservice.dto.spellcheck;

public record SpellCheckResponse(
        SpellCheckResultDto response,

        boolean status
) {
    public String getCorrected() {
        return response != null ? response.corrected() : null;
    }
}