package de.bluplayz.networkhandler.netty.packet;

import com.google.common.base.Charsets;
import de.bluplayz.networkhandler.netty.PacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.UUID;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode( ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> output ) throws Exception {
        int id = byteBuf.readInt();
        Class<? extends Packet> packetClass = PacketHandler.PACKETS.get( id );

        if ( packetClass == null ) {
            throw new NullPointerException( "Couldn't find packet by id " + id );
        }

        Packet packet = packetClass.newInstance();
        int length = byteBuf.readInt();
        packet.uniqueId = UUID.fromString( (String) byteBuf.readCharSequence( length, Charsets.UTF_8 ) );

        packet.read( byteBuf );
        output.add( packet );
    }
}
