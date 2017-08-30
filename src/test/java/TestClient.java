import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.client.NettyClient;

public class TestClient {
    public TestClient() {
        Logger.log( "Loading Application.." );

        //connect to netty
        NettyClient nettyClient = new NettyClient();

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
