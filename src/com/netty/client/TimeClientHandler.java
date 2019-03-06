package com.netty.client;

import com.netty.util.HexUtil;
import com.netty.util.InsertUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class TimeClientHandler extends ChannelHandlerAdapter {


    private byte[] req;
    public TimeClientHandler(){
        req= HexUtil.toBytes("00000000000607030C840004");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        new Thread(){
            @Override
            public void run() {
                super.run();
                while (true){
                    char c ='2';
                    for(int i=2;i<13;i++) {
                        String str = "";
                        if (i == 10) {
                            c = 'A';
                            str = "0000000000060" + c + "030C840004";
                        } else if (i == 11) {
                            c = 'B';
                            str = "0000000000060" + c + "030C840004";
                        } else if (i == 12) {
                            c = 'C';
                            str = "0000000000060" + c + "030C840004";
                        } else if (i < 10) {
                            str = "0000000000060" + i + "030C840004";
                        }
                        System.out.println(str + "," + System.currentTimeMillis());
                        ByteBuf message=null;
                        req = HexUtil.toBytes(str);
                        message = Unpooled.buffer(req.length);
                        message.writeBytes(req);
                        ctx.writeAndFlush(message);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(1000*60*30);//半小时查一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
        /*
        ByteBuf message=null;
        message = Unpooled.buffer(req.length);
        message.writeBytes(req);
        ctx.writeAndFlush(message); */
    }

    //消息在管道中都是以ChannelHandlerContext的形势传递的
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            System.out.println(msg.toString()+"123123");
            ByteBuf in = (ByteBuf) msg;
            byte[] bytes = new byte[17];
            int i=-1;
            //System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
             while (in.isReadable()){
                byte b = in.readByte();
                System.out.print(b+",");
                i++;
                bytes[i] = b;
             }
             System.out.println(i);
            System.out.println(HexUtil.bytesToHexFun1(bytes));
             if (i==16){
                 InsertUtil.insertD(bytes);
             }
        }  finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 出现异常就关闭
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception{
        System.out.println("----------------handler channelReadComplete");
        ctx.flush();
    }

}