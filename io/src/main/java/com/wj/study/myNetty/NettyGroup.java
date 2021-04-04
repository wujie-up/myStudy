package com.wj.study.myNetty;

import com.wj.study.myNetty.bootStrap.ServerBootStrap;
import lombok.Data;

@Data
public class NettyGroup {
    private NettyThread[] threads;
    // 记录work组，是为了在接收到连接后，将client传给worker组的线程处理
    private NettyGroup worker;
    private ServerBootStrap bootStrap;

    public NettyGroup(int num) {
        threads = new NettyThread[num];
        for (int i = 0; i < num; i++) {
            threads[i] = new NettyThread(this);
            threads[i].start();
        }
    }
}
