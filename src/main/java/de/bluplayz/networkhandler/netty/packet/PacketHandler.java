package de.bluplayz.networkhandler.netty.packet;

import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.packet.allpackets.ExitPacket;
import de.bluplayz.networkhandler.netty.packet.allpackets.PingPacket;
import de.bluplayz.networkhandler.netty.packet.allpackets.SimpleMessagePacket;
import io.netty.channel.Channel;

import java.util.Arrays;
import java.util.List;

public class PacketHandler {
    public static final List<Class<? extends Packet>> PACKETS = Arrays.asList(
            PingPacket.class,
            ExitPacket.class,
            SimpleMessagePacket.class
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
            if ( packet instanceof ExitPacket ) {
                channel.close();
                return;
            }
            if ( packet instanceof PingPacket ) {
                sendPacket( packet, channel );
                return;
            }
            if ( packet instanceof SimpleMessagePacket ) {
                SimpleMessagePacket simpleMessagePacket = (SimpleMessagePacket) packet;
                String message = simpleMessagePacket.getMessage();

                Logger.info( "Message from Client -> " + message );
                return;
            }
        }

        if ( type == types.CLIENT ) {
            if ( packet instanceof PingPacket ) {
                PingPacket pingPacket = (PingPacket) packet;
                Logger.info( "Your ping is currently: " + ( System.currentTimeMillis() - pingPacket.time ) );
                return;
            }
        }
    }
}
