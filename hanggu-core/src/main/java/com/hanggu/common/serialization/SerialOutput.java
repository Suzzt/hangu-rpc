package com.hanggu.common.serialization;

import java.io.IOException;

/**
 * @author wuzhenhong
 * @date 2023/7/31 17:15
 */
public interface SerialOutput {

    void writeObject(Object obj) throws IOException;

    void writeString(String text) throws IOException;
}
