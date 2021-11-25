package de.uniba.dsg.wss.api;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for building customized {@link ResponseEntity ResponseEntities}.
 *
 * @author Benedikt Full
 */
public final class ApiResponse {

  /**
   * HTTP header name for the header detailing how long the creation of the request body content
   * took. The header value will detail the processing duration in nanoseconds (therefore
   * Processing-<i>Ns</i>).
   */
  public static final String REQUEST_PROCESSING_NANOS_HEADER_NAME = "Processing-Ns";

  private ApiResponse() {
    throw new AssertionError();
  }

  /**
   * Creates a response builder with the provided status.
   *
   * @param status a valid HttpStatus
   * @return the builder
   * @see #ok()
   */
  public static ApiResponseBuilder status(HttpStatus status) {
    return new ApiResponseBuilder(status);
  }

  /**
   * Creates a response builder with the status set to {@link HttpStatus#OK}.
   *
   * @return the builder
   * @see #status(HttpStatus)
   */
  public static ApiResponseBuilder ok() {
    return status(HttpStatus.OK);
  }

  /**
   * Builder for creating standardized, enriched responses to HTTP requests.
   *
   * @author Benedikt Full
   */
  public static class ApiResponseBuilder {

    private final HttpStatus status;
    private boolean durationHeader;

    private ApiResponseBuilder(HttpStatus status) {
      this.status = requireNonNull(status);
      durationHeader = false;
    }

    /**
     * Can be used to indicate whether the response should contain the duration it took to execute
     * the task provided to {@link #withBody(Supplier)} as an HTTP header.
     *
     * <p>This header is not included by default and must thus be explicitly enabled.
     *
     * @param include {@code true} to include the duration, {@code false} to not include it
     * @return this builder instance
     * @see #withDurationHeader()
     */
    public ApiResponseBuilder withDurationHeader(boolean include) {
      durationHeader = include;
      return this;
    }

    /**
     * Can be used to indicate that the response should contain the duration it took to execute the
     * task provided to {@link #withBody(Supplier)}.
     *
     * <p>The duration will be included as HTTP header with the name {@link
     * #REQUEST_PROCESSING_NANOS_HEADER_NAME}, while the value will be the execution time in
     * nanoseconds.
     *
     * <p>This header is not included by default.
     *
     * @return this builder instance
     * @see #withDurationHeader(boolean)
     */
    public ApiResponseBuilder withDurationHeader() {
      return withDurationHeader(true);
    }

    /**
     * Uses the current state of the builder to create a {@link ResponseEntity}, while the result of
     * the provided {@link Supplier} is used to populate the response body.
     *
     * @param bodySupplier produces the body content, must itself not be {@code null}
     * @param <T> the type of the response body data
     * @return the new response
     */
    public <T> ResponseEntity<T> withBody(Supplier<T> bodySupplier) {
      if (bodySupplier == null) {
        throw new IllegalArgumentException();
      }
      if (durationHeader) {
        long start = System.nanoTime();
        T value = bodySupplier.get();
        long end = System.nanoTime();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(REQUEST_PROCESSING_NANOS_HEADER_NAME, String.valueOf(end - start));
        return ResponseEntity.status(status).headers(responseHeaders).body(value);
      } else {
        return ResponseEntity.status(status).body(bodySupplier.get());
      }
    }
  }
}
