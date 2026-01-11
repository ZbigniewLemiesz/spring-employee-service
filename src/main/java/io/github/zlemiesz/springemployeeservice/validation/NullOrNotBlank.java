package io.github.zlemiesz.springemployeeservice.skill;

/**
 * @author Zbigniew Lemiesz
 */



public @interface NullOrNotBlank {
    String message() default "must be null or not blank";
}
