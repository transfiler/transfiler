package programowanie.zespolowe.tfbeta;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Converter {
    static File OUTPUT_FILE;
    static Converter binary = new Converter();

//    public static String GetBinaryFileName(File path)//wejœcie nazwa pliku wyjœcie nazwaa pliku w postaci 8 bitów oddzielonych spacj¹ w stringu
//    {
//
//        String[] Path = path.getAbsolutePath().split("\\\\");
//        int lenght = Path.length;
//        String temp = Path[lenght-1];
//        String[] Extensions = temp.split("\\.");
//        byte[] bytes1 = Extensions[Extensions.length-1].getBytes();
//        String BinaryFileName = "";
//        for (int i = 0; i < bytes1.length; i++) {
//            String s2 = String.format("%8s", Integer.toBinaryString(bytes1[i] & 0xFF)).replace(' ', '0');//zamienia bajty na ci¹g bitów w stringu
//            //System.out.println(s2);
//            if(i==bytes1.length-1)//dodajemy spacje miedzy oktetami na koncu nie ma spacji
//                BinaryFileName += s2;
//            else BinaryFileName += s2+"";
//        }
//		/*String s2 = "";
//		char nextChar;
//		String[] Test = BinaryFileName.split(" ");
//		for(int i = 0; i < Test.length; i++) //this is a little tricky.  we want [0, 7], [9, 16], etc
//		{
//		     nextChar = (char)Integer.parseInt(Test[i], 2);
//		     s2 += nextChar;
//		}
//		System.out.printf("Wynik2: %s",s2);*/
//        //------------------------
//        return BinaryFileName;
//    }

    public static String Convert2Binary(File file) throws IOException//na wejœciu jest œcie¿ka najlepiej z \\. na wyjsciu jest ci¹g bitów w stringu
    {

        byte[] bytes = binary.readBinaryFile(file);
        StringBuilder BitString = new StringBuilder(bytes.length * Byte.SIZE);
        int len = Byte.SIZE * bytes.length;
        //int counter = 0;
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ ){
            BitString.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');


        }
        String s1 =BitString.toString();
        // String s2 = s.replaceAll("c", " ");
        return s1;
    }

    public static byte[] Convert2file(String bits) throws IOException// na wejœciu ci¹g bitów w stringu po 8 bitów oddzielony spacjami na koncu nie ma spacji. wyjscie brak zapis do pliku.
    {
        int arrayLength = (int) Math.ceil(((bits.length() / (double) 8)));
        String[] bitsArray = new String[arrayLength];
        int k = 0;
        int lastIndex = bitsArray.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            bitsArray[i] = bits.substring(k, k + 8);
            k += 8;
        } // Add the last bit
        bitsArray[lastIndex] = bits.substring(k);


        //String[] bitsArray = bits.split(" ");


        byte[] array = new byte[bitsArray.length];
        int b = 0;

        for (int j = 0; j < array.length; j++) {

            b = Integer.parseInt(bitsArray[j], 2);//z powrotem ## zamiana uwaga 8bitów w stringu na byte[] i zapis do pliku
            array[j] =  (byte) b;


        }

        //binary.writeBinaryFile(array, OUTPUT_FILE);
        return array;
    }

    byte[] readBinaryFile(File f) throws IOException {
        //Path path = Paths.get(aFileName);
        //return Files.readAllBytes(path);
        int size = (int) f.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(f));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }

    public static void writeBinaryFile(byte[] aBytes, File aFileName) throws IOException {


        FileOutputStream out = new FileOutputStream(aFileName);
        out.write(aBytes);
        out.close();
    }

}