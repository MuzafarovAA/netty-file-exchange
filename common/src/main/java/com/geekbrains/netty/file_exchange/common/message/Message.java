package com.geekbrains.netty.file_exchange.common.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        property = "type"
)

public abstract class Message {
}
