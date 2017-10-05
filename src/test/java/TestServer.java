import de.bluplayz.Callback;
import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.ConnectionListener;
import de.bluplayz.networkhandler.netty.NettyHandler;
import de.bluplayz.networkhandler.netty.PacketHandler;
import de.bluplayz.networkhandler.netty.packet.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import packets.EchoPacket;

public class TestServer {
    public TestServer() {
        Logger.log( "Loading Application.." );

        NettyHandler handler = new NettyHandler();
        handler.startServer( 8000, new Callback() {
            @Override
            public void accept(Object... args) {
                start();
            }
        } );

        handler.registerPacketHandler( new PacketHandler() {
            @Override
            public void incomingPacket( Packet packet, Channel channel ) {
                if ( packet instanceof EchoPacket ) {
                    //send back to sender
                    sendPacket( packet, channel );
                }
            }

            @Override
            public void registerPackets() {
                registerPacket( EchoPacket.class );
            }
        } );

        handler.registerConnectionListener( new ConnectionListener() {
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
            new TestServer();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public void start() {
    }
}
