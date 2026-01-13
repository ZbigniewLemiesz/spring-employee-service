package io.github.zlemiesz.springemployeeservice.exception;

/**
 * @author Zbigniew Lemiesz
 */
public class VersionMismatchException extends RuntimeException {
    private final Long requestVersion;
    private final Long actualVersion;

    public VersionMismatchException(Long requestVersion, Long actualVersion) {
        super("Version mismatch: request " + requestVersion + " but actual value is " + actualVersion);
        this.requestVersion = requestVersion;
        this.actualVersion = actualVersion;
    }

    public Long getRequestVersion() {
        return requestVersion;
    }

    public Long getActualVersion() {
        return actualVersion;
    }
}
