package de.bluplayz.networkhandler.netty.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode( ChannelHandlerContext ctx, Packet packet, ByteBuf byteBuf ) throws Exception {
        int id = PacketHandler.PACKETS.indexOf( packet.getClass() );
        if ( id == -1 ) {
            throw new NullPointerException( "Couldn't find id of packet " + packet.getClass().getSimpleName() );
        }

        byteBuf.writeInt( id );
        packet.write( byteBuf );
    }
}
