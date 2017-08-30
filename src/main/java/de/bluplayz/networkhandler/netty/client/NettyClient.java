package de.bluplayz.networkhandler.netty.client;

import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.netty.packet.PacketDecoder;
import de.bluplayz.networkhandler.netty.packet.PacketEncoder;
import de.bluplayz.networkhandler.netty.packet.PacketHandler;
import de.bluplayz.networkhandler.netty.packet.allpackets.ExitPacket;
import de.bluplayz.networkhandler.netty.packet.allpackets.PingPacket;
import de.bluplayz.networkhandler.netty.packet.allpackets.SimpleMessagePacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {
    public static final boolean EPOLL = Epoll.isAvailable();
    public static ExecutorService pool = Executors.newCachedThreadPool();

    Channel channel;

    public NettyClient() {
        Logger.log( "connecting to netty" );

        pool.execute( this::connect );
    }

    public boolean connect() {
        EventLoopGroup eventLoopGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            channel = new Bootstrap()
                    .group( eventLoopGroup )
                    .channel( EPOLL ? EpollSocketChannel.class : NioSocketChannel.class )
                    .handler( new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel( SocketChannel channel ) throws Exception {
                            channel.pipeline().addLast( new PacketEncoder() );
                            channel.pipeline().addLast( new PacketDecoder() );
                            channel.pipeline().addLast( new ClientHandler( NettyClient.this ) );
                        }
                    } ).connect( "localhost", 8000 ).sync().channel();

            BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                if ( line.equalsIgnoreCase( "" ) ) {
                    continue;
                }

                if ( line.startsWith( "!ping" ) ) {
                    Logger.log( "Request ping." );
                    PingPacket pingPacket = new PingPacket();
                    pingPacket.time = System.currentTimeMillis();

                    PacketHandler.sendPacket( pingPacket, channel );
                    continue;
                }
                if ( line.startsWith( "!exit" ) ) {
                    ExitPacket exitPacket = new ExitPacket();

                    PacketHandler.sendPacket( exitPacket, channel );
                    getChannel().closeFuture().syncUninterruptibly();
                    System.exit( 0 );
                    break;
                }
                if ( line.startsWith( "!message " ) ) {
                    SimpleMessagePacket simpleMessagePacket = new SimpleMessagePacket( line.replaceFirst( "!message ", "" ) );

                    PacketHandler.sendPacket( simpleMessagePacket, channel );
                    Logger.info( "Message sent." );
                    continue;
                }

                Logger.error( "Unknown command, try out: !ping, !exit" );
            }
        } catch ( Exception e ) {
            channel = null;
            Logger.error( "failed connecting to netty: " + e.getMessage() );
            Logger.log( "reconnecting in 5 seconds" );

            //try to reconnect after 5 seconds
            scheduleConnect( 5000 );

            //e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
            channel = null;
        }

        return true;
    }

    public Channel getChannel() {
        return channel;
    }

    public void scheduleConnect( int time ) {
        if ( isConnected() ) {
            return;
        }

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Logger.log( "reconnecting..." );
                        connect();
                    }
                },
                time
        );
    }

    public boolean isConnected() {
        return channel != null && channel.isActive();
    }
}
