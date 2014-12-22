package PWA.IndexingPWA;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenIntDoubleHashMap;


public class StartProcess {

	String indexpath;
	Directory dir; 
	Analyzer basicana ;
	IndexWriterConfig config ;
	IndexWriter indexWriter;
	int counter = 0;
	List<String> listoffiles = Collections.synchronizedList(new ArrayList<String>());
	
	//merge indexes
	public StartProcess(String path) throws IOException
	{
		
		indexpath = path;
		dir =  FSDirectory.open(new File(indexpath));
		basicana = new StandardAnalyzer(Version.LUCENE_CURRENT);
		config = new IndexWriterConfig(Version.LUCENE_CURRENT, basicana);
		indexWriter = new IndexWriter(dir,config);
		indexWriter.getConfig().setMaxBufferedDocs(1000000);

		GetFileList();
		
	}
	
	
	public void Close() throws IOException
	{
		indexWriter.close();
		indexWriter.getDirectory().close();
	}
	
	 public void Initialize(int multithread) throws InterruptedException, ExecutionException
	 {
		 // you don't return smt anymore move to runnable
		 	List<Future> list = new ArrayList<Future>();
			ExecutorService executor =  Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

			if(multithread==1)
			{
				for(int i= MainPWA.start ;i<MainPWA.end;i++)
				{ 
					
					final String path = listoffiles.get(i);
					list.add( executor.submit( new Runnable() {
					 public void run() {
					    //	System.out.println("Starting " + path);
							ReadARCs r;
							try {
								r = new ReadARCs(path);
								r.Start();
								Index(r.docslist);
								r.RemoveTmpFiles();
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						
					  }
				}));
	
					
					
					
				}
	
				
			
				for (Future fut :list ) {
					fut.get();
				}
				
	
				executor.shutdown(); 
			}
			else
			{
		
				
				for(int i= MainPWA.start ;i<MainPWA.end;i++){
					String path = listoffiles.get(i);
					ReadARCs r;
					try {
						r = new ReadARCs(path);
						r.Start();
						Index(r.docslist);
						r.RemoveTmpFiles();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}

	
	 public void GetFileList()
	 {

	    Collection<File> fl = FileUtils.listFiles(FileUtils.getFile(MainPWA.mainpath),  FileFilterUtils.fileFileFilter(),FileFilterUtils.fileFileFilter());
		 
		Iterator<File>  fi = fl.iterator();
		
		while(fi.hasNext())
		{
			listoffiles.add(fi.next().getAbsolutePath());
		
			
		}
	    
		Collections.sort(listoffiles);
		System.out.println(listoffiles.size());
		 
	 }
	
	public void Index( List docslist ) throws IOException
	{
		
		
		for(int i =0; i<docslist.size();i++)
		{
			indexWriter.addDocument((Document)docslist.get(i));
			
		}
		
		indexWriter.commit();
		//indexWriter.close();
		counter+=docslist.size();
		System.out.println(counter+ " indexed");

		
		
	}
	
	
	
	public void MergeIndexes(String path)
	{
		File INDEXES_DIR  = new File(path);
		FilenameFilter fileNameFilter = new FilenameFilter() {
			   
            public boolean accept(File dir, String name) {
               if(name.startsWith("index_PWA4"))
               {
                  
                     return true;

               }
               return false;
            }
         };
		
		String [] list = INDEXES_DIR.list(fileNameFilter);
  
		try {
		        Directory indexes[] = new Directory[list.length];
		
		        for (int i = 0; i < list.length; i++) {
		            
		        	System.out.println("Adding: " + list[i]);
		        	
					indexes[i] = FSDirectory.open(new File(INDEXES_DIR.getAbsolutePath() 
						                                    + "/" + list[i]));		
		            
		        }
		
		        System.out.print("Merging added indexes...");
		        indexWriter.addIndexes(indexes);
		        System.out.println("done");
		
		      
		        indexWriter.close();

        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
    
	
	
}
