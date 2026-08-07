package com.midi.gs.lakabe;

import java.io.IOException;
import java.io.InputStream;

public class MidiHelpers {

public static final int MSB_MASK = 1<<7;
    
    public static void readBytes(InputStream stream,byte[] b,int byteCount) {
        try {
        	stream.read(b,0,byteCount);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static boolean bytesMatch(byte[] expectedBytes,byte[] bytes) {
        if (bytes.length<expectedBytes.length)
            return false;
        for (int i=0; i<expectedBytes.length; i++)
            if (expectedBytes[i]!=bytes[i])
                return false;
        return true;
    }
    
    public static int getUnsignedInt(byte[] bytes) {
        return getUnsignedInt(bytes,bytes.length);
    }
    
    public static int getUnsignedInt(byte[] bytes,int byteCount){
        int result = 0;
        for (int i=0; i<byteCount; i++)
            result = result*256 + fixByte(bytes[i]);
        return result;
    }
    
    public static long getUnsignedLong(byte[] bytes) {
        return getUnsignedLong(bytes,bytes.length);
    }
    
    public static long getUnsignedLong(byte[] bytes,int byteCount){
        long result = 0;
        for (int i=0; i<byteCount; i++)
            result = result*256 + fixByte(bytes[i]);
        return result;
    }
    
    public static int fixByte(byte b){
        return b>=0 ? b : 256 + b;
    }
    
    public static boolean byteHasMsbSet(int b) {
        return (b & MSB_MASK)==MSB_MASK; 
    }
}
