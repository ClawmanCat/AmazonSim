package com.groep15.amazonsim.utility;

// Wrapper class for immutable types.
public class Wrapper<T> {
    public Wrapper(T v) { this.value = v; }

    public T value;
}
