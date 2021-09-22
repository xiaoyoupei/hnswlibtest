package com.github.jelmerk.knn.hnsw;

import com.github.jelmerk.knn.IndexException;

/**
 * 抛出，以指示已超过索引的大小。
 * Thrown to indicate the size of the index has been exceeded.
 */
public class SizeLimitExceededException extends IndexException {

    /**
     * 使用指定的详细消息构造一个SizeLimitExceededException。
     * Constructs a SizeLimitExceededException with the specified detail message.
     *
     * @param message the detail message.
     */
    public SizeLimitExceededException(String message) {
        super(message);
    }
}
