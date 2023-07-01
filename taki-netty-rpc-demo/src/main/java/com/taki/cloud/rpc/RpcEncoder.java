package com.taki.cloud.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @ClassName RpcEncoder
 * @Description TODO
 * @Author Long
 * @Date 2023/7/1 18:30
 * @Version 1.0
 */
public class RpcEncoder extends MessageToByteEncoder {


    private Class<?> targetClass;

    public RpcEncoder(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (targetClass.isInstance(o)){
            byte[] bytes = HessianSerialization.serialize(o);
            byteBuf.writeInt(bytes.length); // 先是有4个字节的bytes length，告诉你后续的一条完整的数据，他是有多少个字节
            byteBuf.writeBytes(bytes); // bytes数组，是包含了完整的数据
            // 比如说在这里输出的数据，message length=235（4个字节），bytes=[235个字节]
        }
    }
}
