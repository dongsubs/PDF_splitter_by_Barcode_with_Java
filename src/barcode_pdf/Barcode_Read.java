/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barcode_pdf;

/**
 *
 * @author user
 */



import java.io.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.*;
import com.google.zxing.oned.OneDReader;
import com.google.zxing.oned.Code39Reader;
import com.google.zxing.LuminanceSource;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;


public class Barcode_Read {
    
    public class Margin_Position{
    int Horizontal_Position;
    int Vertical_Position;    
    }

    public static void main(String args[]){
  
    }
    
    public int Height_Depth;
    public int Width_Depth;
    public float Horizontal_Gap;
    public float Vertical_Gap;
    public float Sub_Image_Surplus_Horizontal;
    public float Sub_Image_Surplus_Vertical;

    public int Margin_Quality;
    
    
    Margin_Position Current_Margin = new Margin_Position();
    public int Success_Count;
    int Top_Margin;
    int Bottom_Margin;
    int Left_Margin;
    int Right_Margin;
    
    public String Sub_Folder_String_to_Save ;
    OneDReader Code39_Reader= new Code39Reader();
 
    public void Init_Value(){
     Top_Margin=0;
     Bottom_Margin=0;
     Left_Margin=0;
     Right_Margin=0;
        
    }
  
    public  String Get_Barcode_text(String File_to_Decode) throws IOException {
    
    BinaryBitmap Original_Image = Get_Image_From_File(File_to_Decode);
    
    String Read_Result="";
    int Current_Height_Depth=1; 
    
    while(Current_Height_Depth <= Height_Depth && Read_Result.equals("")){
        Read_Result = Process_Each_Row_of_Image(   Current_Height_Depth,  Original_Image);
        Current_Height_Depth++;
    }
  
    return Read_Result;
    }  
    
    public String Get_Barcode_text_from_each_page_Image(BufferedImage Image_to_Decode) throws IOException {
 
    LuminanceSource  source = new BufferedImageLuminanceSource(Image_to_Decode);
    BinaryBitmap Original_Image = new BinaryBitmap(new HybridBinarizer(source));
    
    String Read_Result="";
    int Current_Height_Depth=1; 
    
    while(Current_Height_Depth <= Height_Depth/2 && Read_Result.equals("")){
        Read_Result = Process_Each_Row_of_Image(Current_Height_Depth,  Original_Image);
        Current_Height_Depth++;
    }
 
    return Read_Result;
 
    }  
    
    
    
    private  String Process_Each_Row_of_Image( int Current_Height_Depth, BinaryBitmap Original_Image){
    BinaryBitmap Croped_Image = null; 
    int Original_Height = Original_Image.getHeight();
    int Original_Width = Original_Image.getWidth();
    int Current_Width_Depth=1; 
    String Read_Result="";
    boolean Suceed_in_Read=false;
  
    int Current_Left_Location=0;
    int Current_Top_Location=0;
    int Current_Width=0;
    int Current_Height=0;

    
    while(Current_Width_Depth <= Width_Depth && Suceed_in_Read==false){
        
        Current_Left_Location=0;
        Current_Top_Location=(Original_Height/Height_Depth)*(Current_Height_Depth-1);
        Current_Width= Original_Width;
        Current_Height=Original_Height/Height_Depth;

        if (Suceed_in_Read==false){
            Croped_Image= Original_Image.crop( Current_Left_Location, Current_Top_Location, Current_Width, Current_Height);
        try{
            Read_Result = Code39_Reader.decode(Croped_Image).toString();
            Suceed_in_Read = true;
        }
        catch( NotFoundException | FormatException IMG_e){
        }
        }

        Current_Left_Location=(Original_Width/Width_Depth)*(Current_Width_Depth-1);
        Current_Width= Original_Width/Width_Depth;
        if (Suceed_in_Read==false){
            Croped_Image= Original_Image.crop( Current_Left_Location, Current_Top_Location, Current_Width, Current_Height);
        try{

            Read_Result = Code39_Reader.decode(Croped_Image).toString();
            Suceed_in_Read = true;
        }
        catch( NotFoundException | FormatException IMG_e){
        //System.out.println("Error: "+IMG_e);
        }
        }
        
        Current_Left_Location=0;
        Current_Width= (Original_Width/Width_Depth)*(Current_Width_Depth);
     
        if (Suceed_in_Read == false) {
            Croped_Image= Original_Image.crop( Current_Left_Location, Current_Top_Location, Current_Width, Current_Height);
        try{
            Read_Result = Code39_Reader.decode(Croped_Image).toString();
            Suceed_in_Read = true;
        }
        catch( NotFoundException | FormatException IMG_e){
        }
        }

        Current_Left_Location=(Original_Width/Width_Depth)*(Current_Width_Depth);
        Current_Width= (Original_Width/Width_Depth)*(Width_Depth-Current_Width_Depth);
        if (Suceed_in_Read == false) {
            Croped_Image= Original_Image.crop( Current_Left_Location, Current_Top_Location, Current_Width, Current_Height);
        try{
            Read_Result = Code39_Reader.decode(Croped_Image).toString();
            Suceed_in_Read = true;
        }
        catch( NotFoundException | FormatException IMG_e){
        }
        }
        //System.out.println(Current_Width_Depth);
        Current_Width_Depth++;
        }
    
      
      if(Suceed_in_Read==false){
          Read_Result="";
      }
      return    Read_Result;
  
    }
    
    private static BinaryBitmap Get_Image_From_File (String File_to_Decode)throws IOException{
    
    File ImageFile = new File(File_to_Decode);
    BufferedImage Barcode_Image = null;

    try{
    Barcode_Image = ImageIO.read(ImageFile);
    }
    catch(IOException IO_e){
      System.out.println("Error: "+IO_e);
    }
    
    LuminanceSource  source = new BufferedImageLuminanceSource(Barcode_Image);
    BinaryBitmap Barcode_Bitmap = new BinaryBitmap(new HybridBinarizer(source));
   
    return Barcode_Bitmap;
    }

}

	
		
      
    
    
    

