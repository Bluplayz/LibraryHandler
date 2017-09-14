package de.bluplayz.networkhandler.netty.server;

import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.NettyHandler;
import de.bluplayz.networkhandler.netty.PacketHandler;
import de.bluplayz.networkhandler.netty.packet.Packet;
import de.bluplayz.networkhandler.netty.packet.defaultpackets.ErrorPacket;
import de.bluplayz.networkhandler.netty.packet.defaultpackets.PacketTransferPacket;
import de.bluplayz.networkhandler.netty.packet.defaultpackets.SetNamePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;

import java.util.Map;

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

        if ( packet instanceof SetNamePacket ) {
            SetNamePacket setNamePacket = (SetNamePacket) packet;

            //remove old entry
            String oldname = "";
            for ( Map.Entry entry : NettyHandler.getClients().entrySet() ) {
                String name = (String) entry.getKey();
                Channel channel = (Channel) entry.getValue();

                if ( channel == ctx.channel() ) {
                    oldname = name;
                    break;
                }
            }

            //remove and add with new name
            if ( !oldname.equalsIgnoreCase( "" ) ) {
                NettyHandler.getClients().remove( oldname );
                NettyHandler.getClients().put( setNamePacket.getName(), ctx.channel() );
            }
        }
        if ( packet instanceof PacketTransferPacket ) {
            PacketTransferPacket packetTransferPacket = (PacketTransferPacket) packet;

            for ( String servername : packetTransferPacket.getTargets() ) {
                if ( !NettyHandler.getClients().containsKey( servername ) ) {
                    //error packet
                    ErrorPacket errorPacket = new ErrorPacket( "The target clientname '" + servername + "' was not found!" );
                    PacketHandler.sendPacket( errorPacket, ctx.channel() );
                    continue;
                }

                //transfer packet
                Channel channel = NettyHandler.getClients().get( servername );
                PacketHandler.sendPacket( packetTransferPacket.getPacket(), channel );
            }
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