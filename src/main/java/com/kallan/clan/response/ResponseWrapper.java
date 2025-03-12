package com.kallan.clan.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ResponseWrapper<T> {
    private String status;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private int statusCode;

    public static <T> ResponseWrapper<T> success(String message, T data) {
        return ResponseWrapper.<T>builder()
                .status("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .statusCode(200)
                .build();
    }

    public static <T> ResponseWrapper<T> error(String message, int statusCode) {
        return ResponseWrapper.<T>builder()
                .status("ERROR")
                .message(message)
                .timestamp(LocalDateTime.now())
                .statusCode(statusCode)
                .build();
    }
}
