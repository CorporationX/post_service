package faang.school.postservice.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    public ErrorResponse (String message){
        this.message = message;
    }

}
