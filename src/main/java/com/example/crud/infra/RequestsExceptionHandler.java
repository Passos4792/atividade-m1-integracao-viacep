package com.example.crud.infra;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.stream.Collectors;
@RestControllerAdvice
public class RequestsExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> notFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(new ExceptionDTO("Produto não encontrado ou inativo",404));
    }
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionDTO> integration(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new ExceptionDTO(ex.getMessage(),ex.getStatus().value()));
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage()).sorted().collect(Collectors.joining("; "));
        return new ResponseEntity<>(new ExceptionDTO(message,400),headers,HttpStatus.BAD_REQUEST);
    }
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>(new ExceptionDTO("Requisição inválida: verifique parâmetros, corpo e método HTTP",
            status.value()),headers,status);
    }
}
