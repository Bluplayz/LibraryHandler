package de.bluplayz.networkhandler.netty.client;

import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.packet.Packet;
import de.bluplayz.networkhandler.netty.packet.PacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<Packet> {

    private Channel channel = null;
    private NettyClient nettyClient;

    public ClientHandler( NettyClient client ) {
        nettyClient = client;
    }

    protected void channelRead0( ChannelHandlerContext ctx, Packet packet ) throws Exception {
        PacketHandler.incomingPacket( packet, channel, PacketHandler.types.CLIENT );
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