package PWA.IndexingPWA;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class Utils {
	
	static public Map<String,String> mapping = new HashMap<String,String>();

	
	static public void ReadCSVandMAP() throws IOException
	{
		File out = new File(MainPWA.mainpath +"mapping.csv");
		String[] temp;
		String dateclean;
		List<String> lines =  FileUtils.readLines(out);
		for(int i=0; i< lines.size(); i++)
		{
			temp = lines.get(i).split(",");
			dateclean = temp[2].replaceAll(":", "");
			dateclean = dateclean.replaceAll("-", "");
			dateclean = dateclean.replaceAll(" ", "");
			mapping.put(temp[1]+dateclean, temp[0]);
		}
		
	}
	public  static String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
	
	
	public static String GetDateByFormat(String data) throws ParseException
	{
		String longDateFormat = "yyyyMMddHHmmss";
		String newlongDateFormat = "yyyy-MM-dd'T'HH:mm:ss";
		Date date = new SimpleDateFormat(longDateFormat).parse(data);
		String formattedDate = new SimpleDateFormat(newlongDateFormat).format(date);
		
		
		return formattedDate;
		
	}

	public static void StatAllCollection(String path) throws IOException
	{
		
		File INDEXES_DIR  = new File(path);
		FilenameFilter fileNameFilter = new FilenameFilter() {
			   
           
            public boolean accept(File dir, String name) {
               if(name.toLowerCase().startsWith("index") )
               {
                  
                     return true;

               }
               return false;
            }
         };
		
		String [] list = INDEXES_DIR.list(fileNameFilter);
		int doccount = 0;

        Directory indexes[] = new Directory[list.length];

        for (int i = 0; i < list.length; i++) {
            
        	System.out.println("Adding: " + list[i]);
        	try{
	        	Directory dir =  FSDirectory.open(new File(INDEXES_DIR.getAbsolutePath() + "/" + list[i]));	
	        	IndexReader indexReader = DirectoryReader.open(dir);
	        	System.out.println(  list[i] + "  " + indexReader.numDocs());
	        	doccount+= indexReader.numDocs();
        	}
        	catch(Exception ex)
        	{
        		System.out.println(  ex + " -- " + list[i]);
        	}
        }

        System.out.println( "Total doc cound " + doccount);
		
		
		
/*
	    IndexReader indexReader = DirectoryReader.open(dir);
	    String field = "docdate";

	    Long mindate = 20120512000000l;
	    Long maxdate = 0l;
	    
	    for(int i=0;i<indexReader.maxDoc();i++)
	    {
	    	Document d = indexReader.document(i);
	    	
	    	Long date = Long.parseLong(d.getField(field).stringValue()) ;
	    	if(date <mindate)
	    		mindate = date;
	    	if(date > maxdate)
	    		maxdate = date;
	    	//System.out.println( i + " date " + date);// " content " +  d.getField("body").stringValue());
	    	
	    	
	    }
		
	    System.out.println( "Max " + maxdate);
	    System.out.println( "Min " + mindate);
	    */
	}
	public static void StatOneCollection(String path) throws IOException
	{
		int doccount = 0;
    	try{
        	Directory dir =  FSDirectory.open(new File(path));	
        	IndexReader indexReader = DirectoryReader.open(dir);
        	doccount= indexReader.numDocs();
    	}
    	catch(Exception ex)
    	{
    		System.out.println(  ex + " -- " + path);
    	}
        

        System.out.println( "Total doc cound " + doccount);
		
		
	}
	
}
