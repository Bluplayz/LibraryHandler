package de.bluplayz.networkhandler.redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisCommandExecutionException;
import com.lambdaworks.redis.RedisConnectionException;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;
import de.bluplayz.color.Color;
import de.bluplayz.logger.Logger;
import de.bluplayz.networkhandler.redis.packet.Packet;
import de.bluplayz.networkhandler.redis.packet.PacketHandler;
import de.bluplayz.networkhandler.redis.packet.PacketListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkHandler {
    public static String HOST = "localhost";
    public static int PORT = 6379;
    public static String PASSWORD = "";
    public static ExecutorService pool = Executors.newCachedThreadPool();

    public static RedisClient client;
    public static StatefulRedisConnection<String, String> connectionCache;
    public static StatefulRedisPubSubConnection<String, String> connectionPubsubListener;
    public static StatefulRedisPubSubConnection<String, String> connectionPubsubPublish;
    public static RedisCommands<String, String> redisCommandsCache;
    public static RedisCommands<String, String> redisCommandsPubsubListener;
    public static RedisCommands<String, String> redisCommandsPubsubPublish;


    /**
     * set hostname from redis connection
     */
    public static void setHostname( String hostname ) {
        HOST = hostname;
    }

    /**
     * set port from redis connection
     */
    public static void setPort( int port ) {
        PORT = port;
    }

    /**
     * set password from redis connection
     */
    public static void setPassword( String password ) {
        PASSWORD = password;
    }

    /**
     * connect with new ip & port to cache
     */
    public static void connectToRedisCache() {
        try {
            if ( PASSWORD.equalsIgnoreCase( "" ) ) {
                client = RedisClient.create( "redis://" + HOST + ":" + PORT + "?timeout=1800ms" );
            } else {
                client = RedisClient.create( "redis://:" + PASSWORD + "@" + HOST + ":" + PORT + "?timeout=1800ms" );
            }

            connectionCache = client.connect();
            redisCommandsCache = connectionCache.sync();
        } catch ( RedisConnectionException | RedisCommandExecutionException e ) {
            client = null;
            connectionCache = null;
            redisCommandsCache = null;
            Logger.error( "cannot connect to redis: " + e.getMessage() );
            return;
        }
    }

    /**
     * connect with new ip & port to pubsub
     */
    public static void connectToRedisPubsub() {
        try {
            if ( PASSWORD.equalsIgnoreCase( "" ) ) {
                client = RedisClient.create( "redis://" + HOST + ":" + PORT + "?timeout=1800ms" );
            } else {
                client = RedisClient.create( "redis://:" + PASSWORD + "@" + HOST + ":" + PORT + "?timeout=1800ms" );
            }

            connectionPubsubListener = client.connectPubSub();
            connectionPubsubPublish = client.connectPubSub();
            redisCommandsPubsubListener = connectionPubsubListener.sync();
            redisCommandsPubsubPublish = connectionPubsubPublish.sync();
        } catch ( RedisConnectionException | RedisCommandExecutionException e ) {
            client = null;
            connectionPubsubListener = null;
            connectionPubsubPublish = null;
            redisCommandsPubsubListener = null;
            redisCommandsPubsubPublish = null;
            Logger.error( "cannot connect to redis: " + e.getMessage() );
            return;
        }
    }

    /**
     * connect with new ip & port
     */
    public static void disconnectFromRedis() {
        if ( !isConnected() ) {
            return;
        }

        connectionCache.close();
        client.shutdown();
        client = null;

        connectionCache = null;
        connectionPubsubListener = null;
        connectionPubsubPublish = null;

        redisCommandsCache = null;
        redisCommandsPubsubListener = null;
        redisCommandsPubsubPublish = null;
    }

    /**
     * check is connected
     */
    public static boolean isConnected() {
        if ( client == null ) {
            return false;
        }

        return true;
    }

    /**
     * set to redis
     */
    public static void setToRedis( String key, String value ) {
        if ( !isConnected() ) {
            return;
        }

        redisCommandsCache.set( key, value );
        redisCommandsCache.expire( key, 30 * 60 ); //30 min
    }


    /**
     * set to redis with expire seconds
     */
    public static void setToRedis( String key, String value, int expireSeconds ) {
        if ( !isConnected() ) {
            return;
        }

        redisCommandsCache.set( key, value );
        redisCommandsCache.expire( key, expireSeconds );
    }


    /**
     * remove from redis
     */
    public static void removeFromRedis( String key ) {
        if ( !isConnected() ) {
            return;
        }

        redisCommandsCache.del( key );
    }


    /**
     * get from redis
     */
    public static String getFromRedis( String key ) {
        if ( !isConnected() ) {
            return null;
        }

        return redisCommandsCache.get( key );
    }

    /**
     * is in redis
     */
    public static boolean isInRedis( String... key ) {
        if ( !isConnected() ) {
            return false;
        }

        return redisCommandsCache.exists( key ) > 0;
    }

    /**
     * is in redis
     */
    public static boolean exists( String... key ) {
        if ( !isConnected() ) {
            return false;
        }

        return redisCommandsCache.exists( key ) > 0;
    }

    public static void subscribe( RedisPubSubListener<String, String> listener, String... channel ) {
        if ( !isConnected() ) {
            return;
        }

        connectionPubsubListener.addListener( listener );
        connectionPubsubListener.sync().subscribe( channel );
    }

    public static boolean channelExists( String channel ) {
        if ( !isConnected() ) {
            return false;
        }

        List<String> channels = redisCommandsPubsubPublish.pubsubChannels( channel );
        return channels.contains( channel );
    }

    public static void sendMessage( final String channel, final String message ) {
        if ( !isConnected() ) {
            return;
        }

        if ( !channelExists( channel ) ) {
            Logger.error( "Channel '" + Color.BOLD + Color.GRAY + channel + Color.RED + "' doesn't exist!" );
            return;
        }

        pool.execute( new Runnable() {
            @Override
            public void run() {
            }
        } );
        redisCommandsPubsubPublish.publish( channel, message );
    }

    public static void sendPacket( final Packet packet, PacketListener.type type ) {
        sendMessage( type.getChannel(), packet.getClass().getSimpleName() + PacketHandler.SERIALIZE_START + packet.toString() );
    }
}
