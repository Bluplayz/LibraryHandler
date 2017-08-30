package de.bluplayz.networkhandler.netty.packet;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface Packet {
    public void read( ByteBuf byteBuf ) throws IOException;
    public void write( ByteBuf byteBuf ) throws IOException;
}