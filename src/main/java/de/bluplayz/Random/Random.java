package de.bluplayz.Random;

import java.text.NumberFormat;

public class Random {
    public static double randomDouble( double min, double max ) {
        double value = min + Math.random() * ( max - min );
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits( 2 );

        return Double.parseDouble( numberFormat.format( value ).replace( ",", "." ) );
    }

    public static int randomInt( double min, double max ) {
        return (int) Math.round( min + Math.random() * ( max - min ) );
    }

    public static boolean chance( double percent ) {
        if ( Random.randomDouble( 0, 100 ) <= percent ) {
            return true;
        }

        return false;
    }
}
