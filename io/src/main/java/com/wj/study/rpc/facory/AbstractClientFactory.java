package com.wj.study.rpc.facory;

import com.wj.study.rpc.protocol.Uri;

public abstract class AbstractClientFactory<T> {
    protected abstract T getCli(Uri uri);
}

