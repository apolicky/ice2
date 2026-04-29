package xyz.apolicky.constraints.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class MeasureUtils {
    private static final Logger log = LoggerFactory.getLogger(MeasureUtils.class);

    public static <T> T measure(String name, Supplier<T> action) {
        long start = System.nanoTime();
        try {
            return action.get();
        } finally {
            long end = System.nanoTime();
            log.info("{} took {} ms", name, (end - start) / 1_000_000);
        }
    }

}
