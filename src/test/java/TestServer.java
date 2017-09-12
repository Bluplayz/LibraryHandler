import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.NettyHandler;
import de.bluplayz.networkhandler.netty.PacketHandler;
import de.bluplayz.networkhandler.netty.packet.Packet;
import io.netty.channel.Channel;
import packets.EchoPacket;

public class TestServer {
    public TestServer() {
        Logger.log( "Loading Application.." );

        NettyHandler handler = new NettyHandler();
        handler.startServer( 8000 );

        handler.registerPacketHandler( new PacketHandler() {
            @Override
            public void incomingPacket( Packet packet, Channel channel ) {
                if ( packet instanceof EchoPacket ) {
                    //send back to sender
                    sendPacket( packet, channel );

                    EchoPacket echoPacket = (EchoPacket) packet;
                    Logger.debug( "echoPacket send back: " + echoPacket.getMessage() );
                }
            }

            @Override
            public void registerPackets() {
                registerPacket( EchoPacket.class );
            }
        } );

        Logger.log( "Application loaded." );
    }

    public static void main( String[] args ) {
        try {
            new TestServer();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }
}
