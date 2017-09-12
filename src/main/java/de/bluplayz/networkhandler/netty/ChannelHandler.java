package de.bluplayz.networkhandler.netty;

import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.client.NettyClient;
import de.bluplayz.networkhandler.netty.packet.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChannelHandler extends SimpleChannelInboundHandler<Packet> {

    private Channel channel = null;
    private NettyClient nettyClient;

    public ChannelHandler( NettyClient client ) {
        nettyClient = client;
    }

    protected void channelRead0( ChannelHandlerContext ctx, Packet packet ) throws Exception {
        de.bluplayz.networkhandler.netty.packet.PacketHandler.incomingPacket( packet, channel, de.bluplayz.networkhandler.netty.packet.PacketHandler.types.CLIENT );
    }

    @Override
    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
        Logger.log( "successfully connected to NettyServer" );
        channel = ctx.channel();
    }

    @Override
    public void channelInactive( ChannelHandlerContext ctx ) throws Exception {
        Logger.log( "disconnected from NettyServer" );
        nettyClient.scheduleConnect( 1000 );
        channel = null;
    }
}