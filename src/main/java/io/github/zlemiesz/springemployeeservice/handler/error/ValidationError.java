package io.github.zlemiesz.springemployeeservice.handler.error;

/**
 * @author Zbigniew Lemiesz
 */
public record ValidationError(String field, String message) {
}
