package PWA.IndexingPWA;

import java.text.ParseException;
import java.text.SimpleDateFormat;



public class ObjectToIndex {
	
	
    /*
     * The pages can be also indexed without creating an object by directly 
     * AddDocument function but some collections need different manipulations 
     * 
     * 
     * */
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
	String title;
	String content;
	String docOriginalId;
	String docId;
	long timestamp; 
	String date;
	String range="";
	String url;
	
	//HERE IT IS BETTER TO MAP QRELS FILES IDS
	public void setId()
	{
		docOriginalId = url+date;
	}
	
	public void setMappedId()
	{

		String id= Utils.mapping.get(docOriginalId);
		docId = id==null?"none":id;

		
	}
	
	public void setUrl( String Url)
	{

		url = Url;
		
	}
	
	public void setDate( String time) throws ParseException
	{
		date = time;
		timestamp = sdf.parse(time).getTime();
	
		
	}
	public void setContent( String bdy)
	{

		content = bdy.replace("\n", " ");
		
	}
	
	public void setTitle( String t)
	{

		title = t;
		
	}
	public void setRange(String ranges)
	{
		range = ranges;
		
		
	}
	 
		

}
