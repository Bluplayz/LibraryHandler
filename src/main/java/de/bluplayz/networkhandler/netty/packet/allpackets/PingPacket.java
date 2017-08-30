package de.bluplayz.networkhandler.netty.packet.allpackets;

import de.bluplayz.networkhandler.netty.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class PingPacket implements Packet {
    public long time = 0;

    @Override
    public void read( ByteBuf byteBuf ) throws IOException {
        time = byteBuf.readLong();
    }

    @Override
    public void write( ByteBuf byteBuf ) throws IOException {
        byteBuf.writeLong( time );
    }
}
