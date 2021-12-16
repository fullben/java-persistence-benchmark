package de.uniba.dsg.wss.api;

import org.springframework.web.context.request.WebRequest;

/**
 * Thrown whenever an API endpoint encounters a malformed request or request parameters (e.g.,
 * missing required parameters).
 *
 * <p>If an exception of this is encountered within a controller, the API will return a response
 * with a {@code 400} status (see {@link ApiExceptionHandler#handleConflict(BadRequestException,
 * WebRequest)}).
 *
 * @author Benedikt Full
 */
public class BadRequestException extends RuntimeException {

  private static final long serialVersionUID = -8156819632323688584L;

  public BadRequestException(String msg) {
    super(msg);
  }
}
