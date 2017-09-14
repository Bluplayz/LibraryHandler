package de.bluplayz.networkhandler.netty;

import de.bluplayz.Callback;
import de.bluplayz.networkhandler.netty.client.NettyClient;
import de.bluplayz.networkhandler.netty.server.NettyServer;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

public class NettyHandler {
    @Getter
    private static ArrayList<PacketHandler> packetHandlers = new ArrayList<>();
    @Getter
    private static HashMap<String, Channel> clients = new HashMap<>();

    @Getter
    @Setter
    private PacketHandler handler;

    @Getter
    @Setter
    private types type = types.CLIENT;

    @Getter
    private static NettyHandler instance;

    @Getter
    private NettyClient nettyClient;
    @Getter
    private NettyServer nettyServer;

    public enum types {
        SERVER,
        CLIENT
    }

    public NettyHandler() {
        instance = this;
    }

    public void connectToServer( String host, int port, Callback callback ) {
        type = types.CLIENT;

        //close server connection
        if ( nettyServer != null ) {
            nettyServer.stopServer();
        }

        //close client connection
        if ( nettyClient != null ) {
            nettyClient.disconnect();
        }

        nettyClient = new NettyClient();
        nettyClient.connect( host, port, callback );
    }

    public void startServer( int port, Callback callback ) {
        type = types.SERVER;

        //close server connection
        if ( nettyServer != null ) {
            nettyServer.stopServer();
        }

        //close client connection
        if ( nettyClient != null ) {
        }

        nettyServer = new NettyServer();
        nettyServer.startServer( port, callback );
    }

    public void registerPacketHandler( PacketHandler handler ) {
        if ( getPacketHandlers().contains( handler ) ) {
            return;
        }

        getPacketHandlers().add( handler );
    }

    public void unregisterPacketHandler( PacketHandler handler ) {
        if ( !getPacketHandlers().contains( handler ) ) {
            return;
        }

        getPacketHandlers().remove( handler );
    }
}
