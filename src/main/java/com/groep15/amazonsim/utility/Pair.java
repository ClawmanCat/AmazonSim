package com.groep15.amazonsim.utility;

import java.util.Objects;

public class Pair<Key, Value> {
    public Key key;
    public Value value;

    public Pair(Key key, Value value) {
    this.key = key;
    this.value = value;
    }

    public Key getKey() {
        return key;
    }

    public Value getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(key, pair.key) &&
                Objects.equals(value, pair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
