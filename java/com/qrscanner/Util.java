package com.qrscanner;

/**
 * Created by wllim on 3/2/16.
 */

import java.nio.ByteBuffer;

public class Util {

    public static String toHexString(byte[] buffer)
    {

        String bufferString = "";

        for (int i = 0; i < buffer.length; i++)
        {

            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1)
            {
                hexChar = "0" + hexChar;
            }

            bufferString += hexChar.toUpperCase() + " ";
        }

        return bufferString;
    }

    public static String toHexString(int i)
    {

        String hexString = Integer.toHexString(i);
        if (hexString.length() % 2 != 0)
        {
            hexString = "0" + hexString;
        }

        return hexString.toUpperCase();
    }


    public static byte[] ReadAndParseArray(byte[] Input, int Length)
    {
        try
        {
            if((Input == null) || (Length < 0) || (Length > Input.length))
                return null;

            if (Length == 0)
                return new byte[0];

            if((Input.length <= 1) && (Length == 1))
            {
                byte[] Temp = Input;
                Input = new byte[0];
                return Temp;
            }

            byte[] Rslt = ChopByteArray(Input, 0, Length);

            Input = ChopByteArray(Input, Rslt.length, (Input.length - Rslt.length));
            return Input;
        }
        catch(Exception e)
        {
            return null;
        }

    }

    public static byte[] ChopByteArray(byte[] Input, int StartingIdx, int Length)
    {
        try
        {
            byte[] TempArray = new byte[Length];
            System.arraycopy(Input, StartingIdx, TempArray, 0, Length);
            return TempArray;
        }
        catch(Exception e)
        {
            return null;
        }
    }


    public static String bytesToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        if (data.length > 0) {
            for (int i = 0; i < data.length; i++) {
                buf.append(byteToHex(data[i]).toUpperCase());
                //buf.append(" ");
            }
        }
    
        return (buf.toString());
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }

    /**
     *  method to convert a byte to a hex string.
     *
     * @param  data  the byte to convert
     * @return String the converted byte
     */
    public static String byteToHex(byte data) {
        StringBuffer buf = new StringBuffer();
        buf.append(toHexChar((data >>> 4) & 0x0F));
        buf.append(toHexChar(data & 0x0F));
        return buf.toString();
    }

    /**
     *  Convenience method to convert an int to a hex char.
     *
     * @param  i  the int to convert
     * @return char the converted char
     */
    public static char toHexChar(int i) {
        if ((0 <= i) && (i <= 9)) {
            return (char) ('0' + i);
        } else {
            return (char) ('a' + (i - 10));
        }
    }

    public static byte[] intToByteArray(int value, int byteRet){

        return (byteRet==1?new byte[] {(byte)value}:new byte[] {
                //(byte)(value >> 24),
                //(byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value });
    }

    public static int byteArrayToInt(byte[] bytes){

        int ret = -1;

        if(bytes.length==1)
            ret =  (bytes[0] & 0xFF);
        if(bytes.length==2)
            ret =  (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);

        //return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
        return ret;
    }

    public static byte[] doubleToByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }

    public static double bytesToDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static int bytesToInt(byte[] b){
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

    public static byte[] intToBytes(int a){
        byte[] ret = new byte[4];
        ret[0] = (byte) (a & 0xFF);
        ret[1] = (byte) ((a >> 8) & 0xFF);
        ret[2] = (byte) ((a >> 16) & 0xFF);
        ret[3] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }

}
