package de.bluplayz.networkhandler.netty.packet.allpackets;

import de.bluplayz.networkhandler.netty.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class ExitPacket implements Packet {
    @Override
    public void read( ByteBuf byteBuf ) throws IOException {
    }

    @Override
    public void write( ByteBuf byteBuf ) throws IOException {
    }
}
