package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String errorCode;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
