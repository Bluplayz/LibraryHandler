package de.bluplayz.networkhandler.netty.server;

import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.packet.Packet;
import de.bluplayz.networkhandler.netty.packet.PacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<Packet> {

    private Channel channel = null;

    protected void channelRead0( ChannelHandlerContext ctx, Packet packet ) throws Exception {
        PacketHandler.incomingPacket( packet, channel, PacketHandler.types.SERVER );
    }

    @Override
    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
        Logger.log( "Client connected successfully" );
        channel = ctx.channel();
    }

    @Override
    public void channelInactive( ChannelHandlerContext ctx ) throws Exception {
        Logger.log( "Client disconnected" );
        channel = null;
    }
}