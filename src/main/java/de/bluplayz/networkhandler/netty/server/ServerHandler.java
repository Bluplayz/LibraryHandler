package de.bluplayz.networkhandler.netty.server;

import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.NettyHandler;
import de.bluplayz.networkhandler.netty.PacketHandler;
import de.bluplayz.networkhandler.netty.packet.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;

public class ServerHandler extends SimpleChannelInboundHandler<Packet> {

    @Getter
    private Channel channel = null;

    @Getter
    private NettyServer nettyServer;

    public ServerHandler( NettyServer nettyServer ) {
        this.nettyServer = nettyServer;
    }

    protected void channelRead0( ChannelHandlerContext ctx, Packet packet ) throws Exception {
        for ( PacketHandler handler : NettyHandler.getPacketHandlers() ) {
            handler.incomingPacket( packet, channel );
        }
    }

    @Override
    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
        Logger.log( "Client connected successfully" );
        channel = ctx.channel();
        NettyHandler.getClients().put( "Channel" + NettyHandler.getClients().size() + 1, ctx.channel() );
    }

    @Override
    public void channelInactive( ChannelHandlerContext ctx ) throws Exception {
        Logger.log( "Client disconnected" );
        channel = null;
        if ( NettyHandler.getClients().containsValue( ctx.channel() ) ) {
            NettyHandler.getClients().put( "Channel" + NettyHandler.getClients().size() + 1, ctx.channel() );
        }
    }
}