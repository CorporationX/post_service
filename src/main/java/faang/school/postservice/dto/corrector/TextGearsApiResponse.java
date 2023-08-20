package faang.school.postservice.dto.corrector;

import java.util.List;

public class TextGearsApiResponse {
    private boolean status;
    private Response response;

    public boolean isStatus() {
        return status;
    }

    public Response getResponse() {
        return response;
    }

    public static class Response {
        private boolean result;
        private List<Error> errors;

        public boolean isResult() {
            return result;
        }

        public List<Error> getErrors() {
            return errors;
        }
    }

    public static class Error {
        private int offset;
        private List<String> better;

        public int getOffset() {
            return offset;
        }

        public List<String> getBetter() {
            return better;
        }
    }
}
