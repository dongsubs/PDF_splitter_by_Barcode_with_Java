/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barcode_pdf;


import com.google.zxing.BinaryBitmap;
import org.apache.pdfbox.util.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.awt.Graphics;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.WindowConstants;
import javax.swing.JFrame;
import javax.swing.JPanel;




import org.apache.pdfbox.pdmodel.PDDocument; 
import org.apache.pdfbox.pdmodel.PDPage; 
import org.apache.pdfbox.pdmodel.PDPageContentStream; 
import org.apache.pdfbox.pdmodel.common.PDRectangle; 
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject; 
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;


/**
 *
 * @author LDS
 */
public class PDF_Converter {
    // 문서 만들기

    Barcode_Read Barcode_Read_Object = new Barcode_Read();
    TreeMap < Integer, String> case_list = new TreeMap< Integer, String>();
   
    PDDocument source_pdf_document= null;
    public String pdf_file_list=null, case_file_list=null;
    int total_case_number=0;

    public static void main(String args[]){
  
    }
    
    public void split_pdf_by_scanned_barcode(File each_source_pdf_file, String input_height_depth,  String input_width_depth){
        Barcode_Read_Object.Height_Depth= Integer.parseInt(input_height_depth);
        Barcode_Read_Object.Width_Depth = Integer.parseInt(input_width_depth);
        set_case_list_from_PDF(each_source_pdf_file);
        divide_pdf_by_case_list(each_source_pdf_file);
   }
     
     public void  set_case_list_from_PDF(File souce_fIle){
     String bacode_result ="";
     BufferedImage each_page_image= null;
     if (case_list.isEmpty()==false) {
         case_list.clear();
     }

       try {
            source_pdf_document= PDDocument.load(souce_fIle);
 //           ArrayList <BufferedImage>  each_image_arrays = PDF_object_to_bufferdimage(source_pdf_document);

            if (source_pdf_document != null) {
		PDFRenderer pdfRenderer = new PDFRenderer(source_pdf_document);
		for (int pageNumber = 0; pageNumber < source_pdf_document.getNumberOfPages(); ++pageNumber) {
                     each_page_image = pdfRenderer.renderImage(pageNumber);
                     bacode_result = Barcode_Read_Object.Get_Barcode_text_from_each_page_Image(each_page_image);  
                     if (!bacode_result.equals("")){
                        check_case_number( pageNumber,  bacode_result);
                     }else {
                      if (pageNumber == 0){
                           case_list.put(0,"unrecognized_case");
                      }
                     }
                }
            } else {
                System.err.println( " File does not exist");
            }
        } 
        catch (Exception e) {
		e.printStackTrace();
	}
    }
     
    
    public void check_case_number(int start_page, String case_number){
        boolean is_new=true;
       Iterator<Integer> it = case_list.keySet().iterator();
        while(it.hasNext()) {
            int current_page = it.next();
            String current_case_number = case_list.get(current_page);
            if (current_case_number.equals(case_number)){               
                is_new=false;
            }
        }
       if (is_new==true){
           case_list.put(start_page,case_number );
       }
       else{
            case_list.put(start_page, case_number + "_duplicated_at_"+start_page );
       }
    }
   public void divide_pdf_by_case_list(File source_file){
        String Source_File_Name = source_file.getName();
 
        total_case_number=0;
        pdf_file_list="";
        case_file_list="";
        Integer old_start_page=0;
        String old_case_number = "";
        Iterator<Integer> it = case_list.keySet().iterator();
        while(it.hasNext()) {
            Integer start_page = it.next();
            String case_number = case_list.get(start_page);
            if (start_page>0){
                save_each_pdf(source_file,  old_case_number, old_start_page, start_page);
            }                
            old_start_page=start_page;
            old_case_number = case_number;
         } 
        Integer last_page = source_pdf_document.getNumberOfPages();
        save_each_pdf(source_file,  old_case_number, old_start_page, last_page);
        case_file_list=case_file_list + total_case_number + " cases have been extracted from "+ Source_File_Name + " \n \n";
        pdf_file_list= Source_File_Name+"  : total "+source_pdf_document.getNumberOfPages() + " page(s)  \n";
        try {
            source_pdf_document.close();
        } catch (IOException ex) {
            Logger.getLogger(PDF_Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
   private void save_each_pdf(File source_file, String case_number, Integer start_page, Integer finish_page){
    String Converted_FIle_Names = "";
    String Source_File_Parent= source_file.getParent();
    
    PDDocument new_pdf_document = new PDDocument();
    try{ 
        for(int current_page=start_page+1; current_page<finish_page; current_page++){
            new_pdf_document.addPage(source_pdf_document.getPage(current_page));
        }
        Converted_FIle_Names = Source_File_Parent+ File.separator + case_number+ ".pdf";
        new_pdf_document.save(Converted_FIle_Names);
        new_pdf_document.close();
        total_case_number=total_case_number+1;
        case_file_list=case_file_list+ case_number + " : " + start_page + "~"+ (finish_page-1) + " (" + (finish_page-start_page-1) + " page(s))  \n";
    }
    catch(Exception e){
         e.printStackTrace();
    }
   }
           
           
 
    

}
