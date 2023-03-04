package com.rbt.test.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiErrorResponse {
    private int code;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp;

    private String message;
}
