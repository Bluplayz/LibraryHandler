package de.bluplayz.networkhandler.redis.packet;

public abstract class PacketListener {

    public type subscribeType = type.ALL;

    public enum type {
        LOBBYSERVER( "LOBBYSERVER" ),
        GAMESERVER( "GAMESERVER" ),
        PROXY( "PROXY" ),
        ALL( "ALL" );

        private String channel = "All";

        type( String channel ) {
            this.channel = channel;
        }

        public String getChannel() {
            return channel;
        }
    }

    public PacketListener() {
    }

    public PacketListener( type type ) {
        subscribeType = type;
    }

    public void onMessage( String channel, String message ) {
        if ( message.split( PacketHandler.SERIALIZE_START ).length < 2 ) {
            return;
        }

        String packetname = message.split( PacketHandler.SERIALIZE_START )[0];
        Packet packet = PacketHandler.getPacketByName( packetname );

        if ( packet == null ) {
            return;
        }

        if ( packet.fromString( message ) == null ) {
            return;
        }

        onPacketReceive( packet.fromString( message ) );
    }

    public abstract void onPacketReceive( Packet packet );

    public type getSubscribeType() {
        return subscribeType;
    }
}
