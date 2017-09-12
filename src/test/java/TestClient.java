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
        nettyHandler.connectToServer( "localhost", 8000 );

        packetHandler = new PacketHandler() {
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
        };

        nettyHandler.registerPacketHandler( packetHandler );

        EchoPacket packet = new EchoPacket( "Hallo, ich bims 1 coole Nachricht! xD" );
        packetHandler.sendPacket( packet );

        Logger.log( "Application loaded." );
    }

    public static void main( String[] args ) {
        try {
            new TestClient();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }
}
