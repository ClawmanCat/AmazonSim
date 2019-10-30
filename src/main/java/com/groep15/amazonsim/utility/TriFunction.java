package com.groep15.amazonsim.utility;

public interface TriFunction <R, X, Y, Z> {
    R execute(X x, Y y, Z z);
}
