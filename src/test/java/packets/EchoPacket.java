package packets;

import com.google.common.base.Charsets;
import de.bluplayz.networkhandler.netty.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class EchoPacket extends Packet {
    @Getter
    @Setter
    private String message = "";

    public EchoPacket() {
    }

    public EchoPacket( String message ) {
        setMessage( message );
    }

    @Override
    public void read( ByteBuf byteBuf ) throws IOException {
        int messageLength = byteBuf.readInt();
        setMessage( (String) byteBuf.readCharSequence( messageLength, Charsets.UTF_8 ) );
    }

    @Override
    public void write( ByteBuf byteBuf ) throws IOException {
        byteBuf.writeInt( message.length() );
        byteBuf.writeCharSequence( message, Charsets.UTF_8 );
    }
}
