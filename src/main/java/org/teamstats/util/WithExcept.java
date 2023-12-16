package org.teamstats.util;

public class WithExcept {
    public static void run(RunnableWithException r) {
        try {
            r.run();
        } catch (Exception ignored) {}
    }

    @FunctionalInterface
    public interface RunnableWithException {
        void run() throws Exception;
    }

    public static <A> A getOrNull(ReturnFunctionWithException<A> r) {
        try {
            return r.run();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FunctionalInterface
    public interface ReturnFunctionWithException<A> {
        A run() throws Exception;
    }
}
