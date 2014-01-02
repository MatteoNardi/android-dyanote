package com.dyanote.android.utils;

/* Just like Runnable, but with an argument.
 * <3 Java */
public interface Func<Arg1> {
    void run(Arg1 arg);
}
