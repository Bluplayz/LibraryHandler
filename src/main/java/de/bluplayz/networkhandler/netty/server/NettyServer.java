package de.bluplayz.networkhandler.netty.server;

import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.packet.PacketDecoder;
import de.bluplayz.networkhandler.netty.packet.PacketEncoder;
import de.bluplayz.networkhandler.netty.packet.PacketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyServer {
    public static final boolean EPOLL = Epoll.isAvailable();
    public static ExecutorService pool = Executors.newCachedThreadPool();

    public NettyServer() {
        Logger.log( "starting netty server" );

        pool.execute( this::startServer );
    }

    public boolean startServer() {
        EventLoopGroup eventLoopGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            new ServerBootstrap()
                    .group( eventLoopGroup )
                    .channel( EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class )
                    .childHandler( new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel( SocketChannel channel ) throws Exception {
                            channel.pipeline().addLast( new PacketEncoder() );
                            channel.pipeline().addLast( new PacketDecoder() );
                            channel.pipeline().addLast( new ServerHandler() );
                        }
                    } ).bind( 8000 ).sync().channel().closeFuture().syncUninterruptibly();
        } catch ( Exception e ) {
            Logger.error( "failed starting netty server: " + e.getMessage() );
            //e.printStackTrace();
            return false;
        } finally {
            eventLoopGroup.shutdownGracefully();
        }

        return true;
    }
}
