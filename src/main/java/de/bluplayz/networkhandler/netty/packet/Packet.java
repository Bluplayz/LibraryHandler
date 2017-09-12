package de.bluplayz.networkhandler.netty.packet;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public abstract class Packet {
    public abstract void read( ByteBuf byteBuf ) throws IOException;
    public abstract void write( ByteBuf byteBuf ) throws IOException;
}