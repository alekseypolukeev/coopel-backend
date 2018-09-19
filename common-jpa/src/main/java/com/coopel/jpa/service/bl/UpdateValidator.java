package com.coopel.jpa.service.bl;

public interface UpdateValidator<E> {
    void validate(E merged, E update);
}
