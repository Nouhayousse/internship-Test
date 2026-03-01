package pfa.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class ErrorResponse {
    private String message;
}
