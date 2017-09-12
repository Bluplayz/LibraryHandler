package de.bluplayz.networkhandler.netty.packet;

import de.bluplayz.networkhandler.netty.packet.defaultpackets.DisconnectPacket;
import io.netty.channel.Channel;

import java.util.Arrays;
import java.util.List;

public class PacketHandler {
    public static final List<Class<? extends Packet>> PACKETS = Arrays.asList(
            DisconnectPacket.class
    );

    public enum types {
        SERVER,
        CLIENT
    }

    public static void sendPacket( Packet packet, Channel channel ) {
        if ( channel == null ) {
            return;
        }

        channel.writeAndFlush( packet, channel.voidPromise() );
    }

    public static void incomingPacket( Packet packet, Channel channel, types type ) {
        if ( type == types.SERVER ) {
            if ( packet instanceof DisconnectPacket ) {
                channel.close();
                return;
            }
        }

        if ( type == types.CLIENT ) {
            if ( packet instanceof DisconnectPacket ) {
                channel.close();
                return;
            }
        }
    }
}
