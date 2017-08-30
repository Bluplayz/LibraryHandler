import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.server.NettyServer;

public class TestServer {
    public TestServer() {
        Logger.log( "Loading Application.." );

        //start netty server
        NettyServer nettyServer = new NettyServer();

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
