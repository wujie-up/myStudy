package com.wj.study.myNetty;

import com.wj.study.myNetty.handler.ChannelHandler;
import lombok.Data;

@Data
public class ChannelContext {
    ChannelHandler channelHandler;
    ChannelContext next;

    public ChannelContext(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }
}
