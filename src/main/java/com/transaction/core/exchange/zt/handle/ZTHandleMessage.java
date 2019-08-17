package com.transaction.core.exchange.zt.handle;

import com.alibaba.fastjson.JSONObject;

public interface ZTHandleMessage {
    /**
     * 实现处理消息的逻辑
     *
     * @param message
     */
    void handle(JSONObject message);

    /**
     * 判断是否可以处理某种类型的消息
     * 消息类型可能会动态配置，所以可能会用到正则
     * 消息处理器可以处理的消息（一种处理器只能处理一种消息）
     * @param json 消息原文
     * @return
     */
    boolean handleType(JSONObject json);
}
