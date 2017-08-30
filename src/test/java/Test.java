import de.bluplayz.logger.Logger;

public class Test {
    public Test() {
        Logger.log( "Loading Application.." );


        Logger.log( "Application loaded." );
    }

    public static void main( String[] args ) {
        try {
            new Test();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }
}
