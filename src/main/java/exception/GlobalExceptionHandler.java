package exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice // Označuje třídu pro globální handling výjimek
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class) // Kterou výjimku odchytává
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", "")); // Získání cesty requestu

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Zde můžeš přidat handlery pro další typy výjimek
    // Např. pro validační chyby (MethodArgumentNotValidException),
    // obecné serverové chyby atd.

    @ExceptionHandler(Exception.class) // Obecný handler pro neočekávané chyby
    public ResponseEntity<Object> handleGlobalException(
            Exception ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred: " + ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        // Logování chyby je zde velmi důležité!
        // log.error("Unhandled exception:", ex);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
