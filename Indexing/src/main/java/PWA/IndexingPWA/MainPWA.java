package PWA.IndexingPWA;


public class MainPWA {

	/**
	 * 
	 * mainpath : the path to the PWA collection, the folder that contains arc files ex:
	 * 			   IAH-20090623180240-08763-awp01.fccn.pt.arc.gz  IAH-20090706074445-17528-awp01.fccn.pt.arc.gz  IAH-20090725160050-26293-awp01.fccn.pt.arc.gz
	 * start, end: index of files # I needed it to launch the application over several machines 
	 * 			   the idea was to create several sub indexes and then merge them
	 *             if you do not need this, you can set start = 0 and end = total number of warc files
	 * type = 0 = index , 1 merge , 2 get collection stats for all sub collections, 3 get stats for merged index
	 * multithread = 0 = False, 1 true 
	 * indexpath: the path to new lucene index
	 * indexnameStart : if sub indexes are not used = empty string
	 * tmppath : path to extract warc files
	 * 
	 * 
	 */
	
	
	public static String mainpath = "/data/pehlivanz/PWA4/"; // can be added to config file
	public static int start = 0; 
	public static int end = 0;
	static int type = 0;
	static int multithread = 0;
	public static String pathtoindex="/data/pehlivanz/index_PWA4";
	public static String indexnameStart="index_PWA4";
	public static String tmppath = "/data/pehlivanz/tmp/"; 
	// I prefered to use my own tmp as tmp on clusters is used by several users
	
	public static void main(String[] args) throws Exception {
		

		mainpath = args[0];
		start= Integer.parseInt(args[1]);
		end = Integer.parseInt(args[2]);
		type = Integer.parseInt(args[3]);
		multithread = Integer.parseInt(args[4]);
		pathtoindex = args[5];
		indexnameStart = args[6];
		tmppath = args[7];
		
		if(type == 0)
		{
			StartProcess sp = new StartProcess(pathtoindex);
			sp.Initialize(multithread);
			sp.Close();
		}
		else if(type == 1)
		{
			String pathtosubindexs = mainpath+"/../";
			StartProcess sp = new StartProcess(pathtoindex);
			sp.MergeIndexes(pathtosubindexs);
		}
		else if(type == 2)// TEST ALL
		{
			Utils.StatAllCollection(mainpath);
		}
		else
		{
			Utils.StatOneCollection(mainpath);
		}
		
		System.out.println("OVER OVER");

	}
	
	

}
