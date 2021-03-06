package com.github.prontera.enums;

import com.github.prontera.util.Capacity;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author Zhao Junjian
 * @date 2020/01/18
 */
public enum OrderState {
    /**
     * invalid value
     */
    INVALID(Integer.MAX_VALUE),
    /**
     * 处理中
     */
    PENDING(0),
    /**
     * 交易成功, 依据fsm的设计可以一个虚状态出现, 即可直接删除存储介质上的数据
     */
    CONFIRMED(1),
    /**
     * 交易超时
     */
    CANCELLED(2),
    /**
     * 交易冲突, 事务未按预期完成
     */
    CONFLICT(3)
    /** LINE SEPARATOR */
    ;

    private static final Map<Integer, OrderState> CACHE;

    private static final DefaultParser PARSER;

    private static final DefaultParser GENTLE_PARSER;

    static {
        final int expectedSize = Capacity.toMapExpectedSize(values().length);
        final Map<Integer, OrderState> builder = new HashMap<>(expectedSize);
        for (OrderState element : values()) {
            builder.put(element.val(), element);
        }
        CACHE = Collections.unmodifiableMap(builder);
        PARSER = new DefaultParser(true);
        GENTLE_PARSER = new DefaultParser(false);
    }

    private final int val;

    OrderState(int val) {
        this.val = val;
    }

    public static OrderState parse(int val) {
        return PARSER.apply(val);
    }

    public static OrderState parseQuietly(int val) {
        return GENTLE_PARSER.apply(val);
    }

    public static <T> OrderState parse(@Nonnull T val, @Nonnull EnumFunction<T, OrderState> function) {
        Objects.requireNonNull(val);
        Objects.requireNonNull(function);
        return function.apply(val);
    }

    public int val() {
        return val;
    }

    public boolean isIntermediateState() {
        return this == PENDING;
    }

    public boolean isFinalState() {
        return this == CONFIRMED || this == CANCELLED || this == CONFLICT;
    }

    private static final class DefaultParser implements EnumFunction<Integer, OrderState> {
        private final boolean throwsIfInvalid;

        private DefaultParser(boolean throwsIfInvalid) {
            this.throwsIfInvalid = throwsIfInvalid;
        }

        @Override
        public OrderState apply(@Nonnull Integer source) {
            Objects.requireNonNull(source);
            OrderState element = CACHE.get(source);
            if (element == null) {
                if (throwsIfInvalid) {
                    throw new NoSuchElementException("No matching constant for [" + source + "]");
                } else {
                    element = INVALID;
                }
            }
            return element;
        }
    }

}

