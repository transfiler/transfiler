
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Converter {
	//UWAGA dodaæ nazwe pliku na pocz¹tek ci¹gu bitów jako ci¹g bitów w stringu najlepiej split po \\ ostatni elem i conversja na bity
	/// œcie¿ki
	  static File FILE_NAME = new File("C:\\Users\\MiniAtom\\Desktop\\BinaryReadWriteJavaProject\\test2.mp3");
	  static File OUTPUT_FILE_NAME = new File("C:\\Users\\MiniAtom\\Desktop\\BinaryReadWriteJavaProject\\testOut2.mp3");//dzia³a dla 1-25mb plikach dalej koniec pamiêci dla stringbuildera
	  static Converter binary = new Converter();
	  //tester
	  //public static void main(String[] args) throws IOException {
		//  Convert2file(Convert2Binary(FILE_NAME));
	//}
	 // public static void main(String[] args) throws IOException {
	//	  System.out.printf("wynik: %s",GetBinaryFileName(FILE_NAME));
	  //}
  public static String GetBinaryFileExtension(File path)//wejœcie cieżka do pliku wyjœcie rozszerzenie pliku w postaci 8 bitów nie oddzielonych spacj¹ w stringu
	{
		//-------------
		
		String[] Path = path.getAbsolutePath().split("/");
		int lenght = Path.length;
		String temp = Path[lenght-1];
		String[] Extensions = temp.split("\\.");
		byte[] bytes1 = Extensions[Extensions.length-1].getBytes();
		String BinaryFileName = "";
		for (int i = 0; i < bytes1.length; i++) {
		    String s2 = String.format("%8s", Integer.toBinaryString(bytes1[i] & 0xFF)).replace(' ', '0');//zamienia bajty na ci¹g bitów w stringu
		//System.out.println(s2);
		    if(i==bytes1.length-1)//nie dodajemy spacje miedzy oktetami na koncu nie ma spacji
			BinaryFileName += s2;
		    else BinaryFileName += s2+"";//nie dodaje spacji
		}

		return BinaryFileName;
	}
	public static String Convert2Binary(File file) throws IOException//na wejœciu jest œcie¿ka najlepiej z \\. na wyjsciu jest ci¹g bitów w stringu
	{ 

	    byte[] bytes = binary.readBinaryFile(file);
	    StringBuilder BitString = new StringBuilder(bytes.length * Byte.SIZE);
	    int len = Byte.SIZE * bytes.length;
	    //int counter = 0;
	    for( int i = 0; i < Byte.SIZE * bytes.length; i++ ){
	    	BitString.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
	    	/*counter++;
	    	if(counter == 8 && i !=(len -1)){
	    		String s = " ";
	    		BitString.append(s);
	    		counter = 0;
	    	}*/
	    	// DO PROGRESS BARU CZY COS W PRZYSZLOSCI
	    	/*if (i == (len/4)) {
				System.out.println("wczytanie 25%");
			}
	    	else if(i == (len/2))
	    	{
	    		System.out.println("wczytanie 50%");
	    	}
	    	else if(i == (len*3/4))
	    	{
	    		System.out.println("wczytanie 75%");
	    	}
	    	else if(i == (len-1))
	    	{
	    		System.out.println("wczytanie 100%");
	    	}*/
	    }
	    String bits =BitString.toString();
	   // String s2 = s.replaceAll("c", " ");
	    return bits;
	  }
	
	public static void Convert2file(String bits) throws IOException// na wejœciu ci¹g bitów w stringu po 8 bitów oddzielony spacjami na koncu nie ma spacji. wyjscie brak zapis do pliku.
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
	    	
	    	/*
	    	if (j == (array.length/4)) {
				System.out.println("zapis 25%");
			}
	    	else if(j == (array.length/2))
	    	{
	    		System.out.println("zapis 50%");
	    	}
	    	else if(j == (array.length*3/4))
	    	{
	    		System.out.println("zapis 75%");
	    	}
	    	else if(j == (array.length-1))
	    	{
	    		System.out.println("zapis 100%");
	    	}
		}*/
		
    	binary.writeBinaryFile(array, OUTPUT_FILE_NAME);
		
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
	  
	  void writeBinaryFile(byte[] aBytes, File aFileName) throws IOException {
	    //Path path = Paths.get(aFileName);
	   // Files.write(path, aBytes); //creates, overwrites
		  
		  FileOutputStream out = new FileOutputStream(aFileName.getAbsolutePath());
		  out.write(aBytes);
		  out.close();
	  }
	  
}
