package de.bluplayz.networkhandler.netty;

import de.bluplayz.Callback;
import de.bluplayz.networkhandler.netty.client.NettyClient;
import de.bluplayz.networkhandler.netty.server.NettyServer;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NettyHandler {
    @Getter
    private static ArrayList<PacketHandler> packetHandlers = new ArrayList<>();
    @Getter
    private static ArrayList<ConnectionListener> connectionListeners = new ArrayList<>();
    @Getter
    private static HashMap<String, Channel> clients = new HashMap<>();
    @Getter
    private static NettyHandler instance;
    @Getter
    @Setter
    private PacketHandler handler;
    @Getter
    @Setter
    private types type = types.CLIENT;
    @Getter
    private NettyClient nettyClient;
    @Getter
    private NettyServer nettyServer;

    public NettyHandler() {
        instance = this;

        Runtime.getRuntime().addShutdownHook( new Thread( new Runnable() {
            @Override
            public void run() {
                if ( getNettyClient() != null ) {
                    getNettyClient().disconnect();
                }
                if ( getNettyServer() != null ) {
                    getNettyServer().stopServer();
                }
            }
        } ) );
    }

    public void connectToServer( String host, int port, Callback callback ) {
        type = types.CLIENT;

        unregisterAllPacketHandler();
        unregisterAllConnectionListener();

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

        unregisterAllPacketHandler();
        unregisterAllConnectionListener();

        //close server connection
        if ( nettyServer != null ) {
            nettyServer.stopServer();
        }

        //close client connection
        if ( nettyClient != null ) {
            nettyClient.disconnect();
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

    public void unregisterAllPacketHandler() {
        getPacketHandlers().clear();
    }

    public void registerConnectionListener( ConnectionListener handler ) {
        if ( getConnectionListeners().contains( handler ) ) {
            return;
        }

        getConnectionListeners().add( handler );
    }

    public void unregisterConnectionListener( ConnectionListener handler ) {
        if ( !getConnectionListeners().contains( handler ) ) {
            return;
        }

        getConnectionListeners().remove( handler );
    }

    public void unregisterAllConnectionListener() {
        getConnectionListeners().clear();
    }

    public String getClientnameByChannel( Channel channel ) {
        for ( Map.Entry entry : clients.entrySet() ) {
            if ( entry.getValue() == channel ) {
                return (String) entry.getKey();
            }
        }

        return "";
    }

    public enum types {
        SERVER,
        CLIENT
    }
}
