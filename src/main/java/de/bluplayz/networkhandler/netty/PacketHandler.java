package de.bluplayz.networkhandler.netty;

import de.bluplayz.networkhandler.netty.packet.Packet;
import de.bluplayz.networkhandler.netty.packet.defaultpackets.DisconnectPacket;
import io.netty.channel.Channel;

import java.util.Collections;
import java.util.List;

public abstract class PacketHandler {
    public static List<Class<? extends Packet>> PACKETS = Collections.singletonList(
            DisconnectPacket.class
    );

    public PacketHandler() {
        registerPackets();
    }

    public void sendPacket( Packet packet ) {
        if ( NettyHandler.getInstance().getType() == NettyHandler.types.CLIENT ) {
            sendPacket( packet, NettyHandler.getInstance().getNettyClient().getChannel() );
        } else {
            for ( Channel channel : NettyHandler.getInstance().getNettyServer().getChannels() ) {
                sendPacket( packet, channel );
            }
        }
    }

    public void sendPacket( Packet packet, Channel channel ) {
        if ( channel == null ) {
            return;
        }

        channel.writeAndFlush( packet, channel.voidPromise() );
    }

    public abstract void incomingPacket( Packet packet, Channel channel );

    public abstract void registerPackets();

    public void registerPacket( Class<? extends Packet> clazz ) {
        if ( PACKETS.contains( clazz ) ) {
            return;
        }

        PACKETS.add( clazz );
    }
}
