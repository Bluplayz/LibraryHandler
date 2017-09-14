package de.bluplayz.networkhandler.netty.packet.defaultpackets;

import com.google.common.base.Charsets;
import de.bluplayz.networkhandler.netty.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
public class SetNamePacket extends Packet {
    @Getter
    @Setter
    private String name = "";

    @Override
    public void read( ByteBuf byteBuf ) throws IOException {
        int length = byteBuf.readInt();
        setName( (String) byteBuf.readCharSequence( length, Charsets.UTF_8 ) );
    }

    @Override
    public void write( ByteBuf byteBuf ) throws IOException {
        byteBuf.writeInt( getName().length() );
        byteBuf.writeCharSequence( getName(), Charsets.UTF_8 );
    }
}
