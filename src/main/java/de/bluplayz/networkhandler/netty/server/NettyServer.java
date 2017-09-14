package de.bluplayz.networkhandler.netty.server;

import de.bluplayz.Callback;
import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.NettyHandler;
import de.bluplayz.networkhandler.netty.packet.PacketDecoder;
import de.bluplayz.networkhandler.netty.packet.PacketEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyServer {
    public static final boolean EPOLL = Epoll.isAvailable();
    public static ExecutorService pool = Executors.newCachedThreadPool();

    @Getter
    private EventLoopGroup eventLoopGroup;
    @Getter
    private ServerBootstrap bootstrap;
    @Getter
    private ChannelFuture future;

    @Getter
    @Setter
    private int port = 8000;

    public void startServer( int port, Callback callback ) {
        Logger.log( "starting netty server" );
        setPort( port );

        if ( getFuture() != null ) {
            stopServer();
        }

        pool.execute( () -> {
            eventLoopGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
            try {
                bootstrap = new ServerBootstrap()
                        .group( eventLoopGroup )
                        .channel( EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class )
                        .childHandler( new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel( SocketChannel channel ) throws Exception {
                                channel.pipeline().addLast( new PacketEncoder() );
                                channel.pipeline().addLast( new PacketDecoder() );
                                channel.pipeline().addLast( new ServerHandler( NettyServer.this ) );
                            }
                        } );

                future = bootstrap.bind( port );
                callback.accept();
                future.sync().channel().closeFuture().syncUninterruptibly();
            } catch ( Exception e ) {
                Logger.error( "failed starting netty server: " + e.getMessage() );
                //e.printStackTrace();
            } finally {
                eventLoopGroup.shutdownGracefully();
            }
        } );
    }

    public void stopServer() {
        if ( getFuture() == null ) {
            return;
        }

        for ( Channel channel : NettyHandler.getClients().values() ) {
            channel.close();
        }

        future = null;
        bootstrap = null;
        eventLoopGroup = null;
    }
}
