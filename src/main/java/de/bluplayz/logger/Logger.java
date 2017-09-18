package de.bluplayz.logger;

import de.bluplayz.color.Color;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Logger {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "HH:mm:ss" ); // "dd.MM.yyyy HH:mm:ss"
    private static boolean timeZoneSetted = false;

    /**
     * log error message
     */
    public static void error( String message ) {
        if ( !timeZoneSetted ) {
            setTimeZone();
        }

        System.out.println( Color.RESET + Color.CYAN + "[" + simpleDateFormat.format( new Date() ) + "] " + Color.RED + message + Color.WHITE );
    }

    /**
     * log normal message
     */
    public static void info( String message ) {
        if ( !timeZoneSetted ) {
            setTimeZone();
        }

        System.out.println( Color.RESET + Color.CYAN + "[" + simpleDateFormat.format( new Date() ) + "] " + Color.RESET + message + Color.WHITE );
    }

    /**
     * log normal message
     */
    public static void log( String message ) {
        if ( !timeZoneSetted ) {
            setTimeZone();
        }

        System.out.println( Color.RESET + Color.CYAN + "[" + simpleDateFormat.format( new Date() ) + "] " + Color.RESET + message + Color.WHITE );
    }

    /**
     * log warning message
     */
    public static void warning( String message ) {
        if ( !timeZoneSetted ) {
            setTimeZone();
        }

        System.out.println( Color.RESET + Color.CYAN + "[" + simpleDateFormat.format( new Date() ) + "] " + Color.RESET + Color.YELLOW + message + Color.WHITE );
    }

    /**
     * log debug message
     */
    public static void debug( String message ) {
        if ( !timeZoneSetted ) {
            setTimeZone();
        }

        System.out.println( Color.RESET + Color.CYAN + "[" + simpleDateFormat.format( new Date() ) + "] " + Color.RESET + Color.CYAN + message + Color.WHITE );
    }

    /**
     * set timezone
     */
    private static void setTimeZone() {
        simpleDateFormat.setTimeZone( TimeZone.getTimeZone( "Europe/Berlin" ) );

        timeZoneSetted = true;
    }
}
