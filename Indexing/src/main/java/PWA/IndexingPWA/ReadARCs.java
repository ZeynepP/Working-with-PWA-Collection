package PWA.IndexingPWA;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.archive.io.ArchiveRecord;
import org.archive.io.arc.ARCReader;
import org.archive.io.arc.ARCReaderFactory;
import org.archive.io.arc.ARCRecord;
import org.archive.io.arc.ARCRecordMetaData;
import org.xml.sax.ContentHandler;
public class ReadARCs {
	
	
	private final byte[] buffer = new byte[1024 * 16];

	private final ByteArrayOutputStream contentBuffer = new ByteArrayOutputStream(1024 * 16);
	
	private static final int BUFFER_SIZE = 1024 * 16;


	@SuppressWarnings("unchecked")
	List docslist = Collections.synchronizedList(new ArrayList());
	
	
	private static final String HTMLCONTENTTYPE = "text/html";
	/**
	 * An ARC reader to iterate over each ARC records from the input
	 */
	private ARCReader r;
	/**
	 * Should we digest as we read?
	 */
	private boolean digest = false;

	/**
	 * Should the parse be strict?
	 */
	private boolean strict = false;

	File temp;
	
	/**
	 * Should we parse header
	 */
	private boolean parse = false;
	
	public  ReadARCs(String file)
	{
		// it says ARCReaderFactory handles compressed and uncompressed arc files
		// checked it and it is not working so uncompressing to tmp
		
		try{
			
			
			if(file.endsWith("gz"))
			{
				File f = new File(file);
				String name = GzipUtils.getUncompressedFilename(f.getName());
				gunzipIt(f,name);
				temp = new File(MainPWA.tmppath+name);
			}
			else
				temp = new File(file);
			
			r = ARCReaderFactory.get(temp);
			r.setStrict(strict);
			r.setParseHttpHeaders(parse);
			r.setDigest(digest);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();   
		}
		
	
	}
	
	public void gunzipIt(File file,String name) throws IOException{
		 
	     byte[] buffer = new byte[BUFFER_SIZE];
	     GZIPInputStream gzis = null;
	     FileOutputStream out = null;
	     try{
	 
	    	 
	    	  	gzis = new GZIPInputStream(new FileInputStream(file));
	    	  	out = new FileOutputStream( MainPWA.tmppath + name);
	 
		        int len;
		        while ((len = gzis.read(buffer)) > 0) {
		        	out.write(buffer, 0, len);
		        }

	 
	    }catch(IOException ex){
	       System.out.println("IN gunzipIt " + ex.toString());
	       ex.printStackTrace();   
	    }
	     finally{
	    	 gzis.close();
		     out.close();
	    	 
	     }
	   } 
	
	
	public void ParseHtml(ByteArrayInputStream input, String Url, String date) 
	{
		   try {
			   
			   
				LinkContentHandler linkHandler = new LinkContentHandler();
		        ContentHandler textHandler = new BodyContentHandler(100*1024*1024);
		        ToHTMLContentHandler toHTMLHandler = new ToHTMLContentHandler();
		        TeeContentHandler teeHandler = new TeeContentHandler(linkHandler, textHandler, toHTMLHandler);
		        Metadata metadata = new Metadata();
		        ParseContext parseContext = new ParseContext();
		        HtmlParser parser = new HtmlParser();
		        
		    
		        parser.parse(input, teeHandler, metadata, parseContext);
		        
		        ObjectToIndex obj = new ObjectToIndex();
		        
		        obj.setContent(textHandler.toString().trim());
		       
				
		        obj.setDate(date);
				
				obj.setUrl(Url);
				
		        obj.setTitle(metadata.get("title")==null?"":metadata.get("title"));
		        
		        obj.setId();
		        
		    //    obj.setMappedId(); // for qrels
		        
		        AddDocument(obj);
		        
		   }
        catch (Exception e) {
			
			System.out.println("IN ParseHtml " + e.toString() + " -- " + Url + " -- " + date);
			//System.out.println(Utils.getStringFromInputStream(input));
		}
	}
	
	public void RemoveTmpFiles()
	{
		temp.delete();
		temp.deleteOnExit();
		//System.out.println("Delete  " + temp.getAbsolutePath());
	}
	
	public void Start() throws IOException
	{
		
		Iterator<ArchiveRecord> ite = r.iterator();
		String url;
		String date;
		ARCRecord rec = null;
		ARCRecordMetaData arcData;
		String mimetype;
		byte[] buffer;
		boolean isError = false;
		// move through all the ARC records
		while (ite.hasNext()) {
			
			try{
			
				rec = (ARCRecord) ite.next();
				arcData = rec.getMetaData();
				mimetype = arcData.getMimetype();
	
				if(mimetype.equals( HTMLCONTENTTYPE))
				{
					isError = false;
					url = rec.getHeader().getUrl();
					date = rec.getHeader().getDate();
	
	
					buffer = new byte[20];
	
					int k = rec.read(buffer, 0, 20);
					if( new String(buffer, "UTF-8").contains("40"))
						isError = true;
	
					if(!isError)
					{
					
							rec.skipHttpHeader();
					
							// Read in first block. If mimetype still null, look for MAGIC.
							int len = rec.read(this.buffer, 0, this.buffer.length);
					
							// Reset our contentBuffer so can reuse. Over the life of an ARC
							// processing will grow to maximum record size.
							this.contentBuffer.reset();
					
							int total = 0;
							while ((len != -1)) {
								total += len;
								this.contentBuffer.write(this.buffer, 0, len);
								len = rec.read(this.buffer, 0, this.buffer.length);
			
							}
					
							
							byte[] contentBytes = this.contentBuffer.toByteArray();
			
							ByteArrayInputStream in = new ByteArrayInputStream(contentBytes);
							
							ParseHtml(in, url, date);
								
						
					}
			
				}
			
			 }catch(IOException ex){
			     
			       ex.printStackTrace();   
			    }
			finally{
				rec.close();
			}
		}
		
		
		r.close();
	}
	

	public void AddDocument(ObjectToIndex o)
	{
	
		 
			
			Document document = new Document() ;
			
			FieldType texts = new FieldType();
			texts.setStored(true);
			texts.setIndexed(true);
			texts.setStoreTermVectors(true);
			texts.setTokenized(true);
			
			FieldType numbers = new FieldType();
			numbers.setStored(true);
			numbers.setIndexed(true);
			numbers.setStoreTermVectors(true);
			numbers.setTokenized(false);

			
			document.add(new Field("url",o.url, numbers));
			document.add(new Field("docid",String.valueOf(o.docId), numbers));
			document.add(new Field("docname",String.valueOf(o.docId), numbers));
			document.add(new Field("body", o.content, texts));
			document.add(new Field("doctitle", o.title ,texts));
			document.add(new Field("docdate", o.date ,numbers));
			
			LongField field = new LongField("docdatenum", 0L, Field.Store.YES);
			field.setLongValue(o.timestamp);
		    document.add(field);
		    
	
		    docslist.add(document);

		
	}
	
	
	

}
