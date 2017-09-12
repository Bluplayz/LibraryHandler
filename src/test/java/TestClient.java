import de.bluplayz.Callback;
import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.NettyHandler;
import de.bluplayz.networkhandler.netty.PacketHandler;
import de.bluplayz.networkhandler.netty.packet.Packet;
import io.netty.channel.Channel;
import lombok.Getter;
import packets.EchoPacket;

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
                Logger.debug( "DEBUG1" );
                if ( packet instanceof EchoPacket ) {
                    EchoPacket echoPacket = (EchoPacket) packet;
                    Logger.debug( "echoPacket come back: " + echoPacket.getMessage() );
                }
            }

            @Override
            public void registerPackets() {
                Logger.debug( "DEBUG2" );
                registerPacket( EchoPacket.class );
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
        EchoPacket packet = new EchoPacket( "Hallo, ich bims 1 coole Nachricht! xD" );
        getPacketHandler().sendPacket( packet );
    }
}
