package de.bluplayz.networkhandler.redis.packet;

import com.google.gson.Gson;

public abstract class Packet {
    public static final Gson GSON = new Gson();
    private static final String SERIALIZE_ENDROW = "Z6f8cvJVEsZ4reNjz2kLJLAbSCF3QVaW";
    private static final String SERIALIZE_SPLIT = "8GGfZtmv4y4JGPkyWjfnLXTU6rUkAp4u";

    public String toString() {
        return GSON.toJson( this );
    }

    public Packet fromString( String stringdata ) {
        stringdata = stringdata.replaceFirst( getName() + PacketHandler.SERIALIZE_START, "" );
        return GSON.fromJson( stringdata, getClass() );
    }

    public String getName() {
        return getClass().getSimpleName();
    }
}
