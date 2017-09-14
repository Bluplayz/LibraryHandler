package de.bluplayz.networkhandler.netty.client;

import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.ConnectionListener;
import de.bluplayz.networkhandler.netty.NettyHandler;
import de.bluplayz.networkhandler.netty.PacketHandler;
import de.bluplayz.networkhandler.netty.packet.Packet;
import de.bluplayz.networkhandler.netty.packet.defaultpackets.DisconnectPacket;
import de.bluplayz.networkhandler.netty.packet.defaultpackets.ErrorPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;

public class ClientHandler extends SimpleChannelInboundHandler<Packet> {

    @Getter
    private Channel channel = null;

    @Getter
    private NettyClient nettyClient;


    public ClientHandler( NettyClient client ) {
        nettyClient = client;
    }

    protected void channelRead0( ChannelHandlerContext ctx, Packet packet ) throws Exception {
        if ( packet instanceof ErrorPacket ) {
            String message = ( (ErrorPacket) packet ).getErrorMessage();
            Logger.error( message );
        }

        if ( packet instanceof DisconnectPacket ) {
            channel.close();
        }

        for ( PacketHandler handler : NettyHandler.getPacketHandlers() ) {
            handler.incomingPacket( packet, channel );
        }
    }

    @Override
    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
        Logger.log( "successfully connected to NettyServer" );
        channel = ctx.channel();
        getNettyClient().setChannel( ctx.channel() );

        if ( NettyHandler.getPacketHandlers().size() > 0 ) {
            if ( PacketHandler.packetsToSend.size() > 0 ) {
                for ( Packet packet : PacketHandler.packetsToSend ) {
                    NettyHandler.getPacketHandlers().get( 0 ).sendPacket( packet );
                }
            }
        }

        for ( ConnectionListener handler : NettyHandler.getConnectionListeners() ) {
            handler.channelConnected( ctx );
        }
    }

    @Override
    public void channelInactive( ChannelHandlerContext ctx ) throws Exception {
        Logger.log( "disconnected from NettyServer" );
        nettyClient.scheduleConnect( 1000 );
        channel = null;
        getNettyClient().setChannel( null );

        for ( ConnectionListener handler : NettyHandler.getConnectionListeners() ) {
            handler.channelDisconnected( ctx );
        }
    }
}