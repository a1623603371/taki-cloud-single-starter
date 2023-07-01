package com.taki.cloud.rpc.sevice;

import com.taki.cloud.rpc.HessianSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName RpcDeoder
 * @Description TODO
 * @Author Long
 * @Date 2023/6/30 16:30
 * @Version 1.0
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private static  final int MESSAGE_LENGTH_BYTES = 4;

    private  static  final int MESSAGE_LENGTH_VALID_MINIMUM_VALUE = 0;


    private Class<?> targetClass;

    public RpcDecoder(Class<?> targetClass) {
        this.targetClass = targetClass;
    }


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        // 效应消息长度，需要达到 4位
        if (byteBuf.readableBytes() < MESSAGE_LENGTH_BYTES){

            return;
        }

        //对于byteBuf当前可以读的readerIndex，mark标记
     // 后续我可以通过这个mark标记，我可以找回来在发起read读取之前的一个readerIndex位置
        byteBuf.markReaderIndex();
        // 读取4个字节的int，int代表了你的消息bytes长度
        int messageLength = byteBuf.readInt();
        // 如果说此时消息长度是小于0，说明此时通信已经出现了故障
        if (messageLength < MESSAGE_LENGTH_VALID_MINIMUM_VALUE){
                channelHandlerContext.close();
        }

        if(byteBuf.readableBytes() < messageLength){

            byteBuf.resetReaderIndex();
            return;


        }

            // 反序列化
        byte[] bytes = new byte[messageLength];
        byteBuf.readBytes(bytes);

        Object object = HessianSerialization.deserialize(bytes,targetClass);

        list.add(object);
    }
}
