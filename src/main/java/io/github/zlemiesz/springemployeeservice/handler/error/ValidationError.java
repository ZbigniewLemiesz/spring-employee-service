package io.github.zlemiesz.springemployeeservice.error;

/**
 * @author Zbigniew Lemiesz
 */
public record ValidationError(String field, String message) {
}
