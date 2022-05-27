package de.uniba.dsg.wss.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Provides exception handling for certain exceptions expected to be encountered in the API.
 *
 * @author Benedikt Full
 */
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LogManager.getLogger(ApiExceptionHandler.class);

  /**
   * Logs the given exception and returns a {@code 400 Bad Request} response.
   *
   * @param ex the exception
   * @param request the request that caused the exception
   * @return the <i>bad request</i> response
   */
  @ExceptionHandler(value = {BadRequestException.class})
  protected ResponseEntity<Object> handleConflict(BadRequestException ex, WebRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    log(ex, status);
    return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), status, request);
  }

  private void log(Exception e, HttpStatus status) {
    LOG.warn("{}: {} (Responding with: {})", e.getClass().getSimpleName(), e.getMessage(), status);
  }
}
