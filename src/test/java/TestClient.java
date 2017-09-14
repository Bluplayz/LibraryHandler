import de.bluplayz.Callback;
import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.ConnectionListener;
import de.bluplayz.networkhandler.netty.NettyHandler;
import de.bluplayz.networkhandler.netty.PacketHandler;
import de.bluplayz.networkhandler.netty.packet.Packet;
import de.bluplayz.networkhandler.netty.packet.defaultpackets.PacketTransferPacket;
import de.bluplayz.networkhandler.netty.packet.defaultpackets.SetNamePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import packets.EchoPacket;

import java.util.ArrayList;

public class TestClient {
    @Getter
    private PacketHandler packetHandler;

    @Getter
    private NettyHandler nettyHandler;

    public TestClient() {
        Logger.log( "Loading Application.." );

        //connect to netty
        nettyHandler = new NettyHandler();
        nettyHandler.connectToServer( "localhost", 8000, new Callback() {
            @Override
            public void accept() {
                start();
            }
        } );

        nettyHandler.registerPacketHandler( packetHandler = new PacketHandler() {
            @Override
            public void incomingPacket( Packet packet, Channel channel ) {
                if ( packet instanceof EchoPacket ) {
                    EchoPacket echoPacket = (EchoPacket) packet;
                    Logger.debug( "echoPacket come back: " + echoPacket.getMessage() );
                }
            }

            @Override
            public void registerPackets() {
                registerPacket( EchoPacket.class );
            }
        } );

        nettyHandler.registerConnectionListener( new ConnectionListener() {
            @Override
            public void channelConnected( ChannelHandlerContext ctx ) {
                Logger.debug( "Channel Connected with listener :)" );
            }

            @Override
            public void channelDisconnected( ChannelHandlerContext ctx ) {
                Logger.debug( "Channel Disconnected with listener :)" );
            }
        } );

        Logger.log( "Application loaded." );
    }

    public static void main( String[] args ) {
        try {
            new TestClient();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public void start() {
        EchoPacket echoPacket = new EchoPacket( "Hallo, ich bims 1 reine Kartoffel!" );
        getPacketHandler().sendPacket( echoPacket );

        SetNamePacket setNamePacket = new SetNamePacket( "Server75" );
        getPacketHandler().sendPacket( setNamePacket );

        ArrayList<String> targets = new ArrayList<>();
        targets.add( "Server75" );
        targets.add( "Server31" );
        targets.add( "Server02" );
        PacketTransferPacket packetTransferPacket = new PacketTransferPacket( targets, echoPacket );
        getPacketHandler().sendPacket( packetTransferPacket );
    }
}
