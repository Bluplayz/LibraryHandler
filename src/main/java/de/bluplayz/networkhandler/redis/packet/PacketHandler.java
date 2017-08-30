package de.bluplayz.networkhandler.redis.packet;

import de.bluplayz.networkhandler.redis.NetworkHandler;

import java.util.HashMap;

public class PacketHandler {
    public static final String SERIALIZE_START = "8GGfZtmv4y4JGPkyWGPkyWjfnLXTU6rgjfnLXTU6rgUkAp4uAp4u8GGfZtmv4y4JGPkyWjfnLXTU8GGfZtmv4y4JUk6rgUkAp4u";
    public static HashMap<String, Packet> packets = new HashMap<>();

    public static void addPacket( Packet packet ) {
        if ( packets.containsKey( packet.getClass().getSimpleName() ) ) {
            return;
        }

        //add old.packet
        packets.put( packet.getClass().getSimpleName(), packet );
    }

    public static void sendPacket( Packet packet ) {
        //NetworkHandler.sendPacket( old.packet, PacketListener.type.ALL );
    }

    public static void sendPacket( Packet packet, PacketListener.type type ) {
        //NetworkHandler.sendPacket( old.packet, type );
    }

    public static void addPacketListener( final PacketListener listener ) {
        NetworkHandler.pool.execute( new Runnable() {
            @Override
            public void run() {
                //NetworkHandler.subscribe( listener, listener.getSubscribeType().getChannel() );
            }
        } );
    }

    public static Packet getPacketByName( String packetname ) {
        if ( !packets.containsKey( packetname ) ) {
            return null;
        }

        return packets.get( packetname );
    }
}
