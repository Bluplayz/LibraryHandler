package de.bluplayz.networkhandler.netty.packet.allpackets;

import com.google.common.base.Charsets;
import de.bluplayz.networkhandler.netty.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class SimpleMessagePacket implements Packet {
    @Getter
    @Setter
    private String message = "";

    public SimpleMessagePacket() {
    }

    public SimpleMessagePacket( String message ) {
        this.message = message;
    }

    @Override
    public void read( ByteBuf byteBuf ) throws IOException {
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes( bytes );

        message = new String( bytes, Charsets.UTF_8 );
    }

    @Override
    public void write( ByteBuf byteBuf ) throws IOException {
        byte[] bytes = message.getBytes();
        byteBuf.writeInt( bytes.length );
        byteBuf.writeBytes( bytes );
    }
}
