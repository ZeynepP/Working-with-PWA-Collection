
library(bfast) 
library(sqldf)
library(XML)

PAHTOSAVE= "/home/pehlivanz/GoogleTrends/PWATopicsDAILY.xml"
data = sqldf("SELECT * FROM Queries_Values INNER JOIN Queries ON Queries.id = Queries_Values.q_id", dbname = "/home/pehlivanz/9")
counter = 0
REPEAT = 0;
for( i in 1:667)
{
	
	querydata <- data[data$q_id==i,]
	query = querydata$query[1]
	trendsdata <- querydata[order(querydata$id),]$trends # ordered by week id
	t <- as.data.frame(trendsdata)
	if( length(trendsdata) == 104)   {REPEAT = 7} 	else  {REPEAT = 30} # to get real id freq = 2 * 4 because by month 
	index <- rep(seq_len(nrow(t)), each = REPEAT)
	
	ts = ts(t[index,],start=1,frequency=2)
	fit <- bfast(ts,h=0.15,max.iter=1)
	trendsdataDAILY <- as.data.frame(t[index,])

	
	min = 1;
	max =nrow(trendsdataDAILY);
	
	breaks <- breakdates(fit$output[[1]]$bp.Vt) * 2 ;

	print(query)
	print(breaks)
	if(is.na(breaks)[1] == TRUE)
	{
		counter = counter + 1
		top = newXMLNode("top") # las interval 
		newXMLNode("num",paste("Number : ", counter),parent = top)
		newXMLNode("title",paste(query,"@",min,"_",max,sep=""),parent = top)
		newXMLNode("desc",paste("Description : ", counter),parent = top)
		from = querydata[querydata$id==min,]$week
		to = querydata[querydata$id==round(max/REPEAT),]$week
		newXMLNode("narr", paste("from", from, "to" ,to ),parent = top)
		write(toString.XMLNode(top),PAHTOSAVE,append=TRUE)
	} else {
		for( b in breaks)
		{
			if(sum(trendsdataDAILY[min:b,]) > 0)
			{
				counter = counter + 1
				top = newXMLNode("top")
				newXMLNode("num",paste("Number : ", counter),parent = top)
				newXMLNode("title",paste(query,"@",min,"_",b,sep=""),parent = top)
				newXMLNode("desc",paste("Description : ", counter),parent = top)
				from = querydata[querydata$id==round(min/REPEAT),]$week
				to = querydata[querydata$id==round(b/REPEAT),]$week
				newXMLNode("narr", paste("from", from, "to" ,to ),parent = top)
				write(toString.XMLNode(top),PAHTOSAVE,append=TRUE)
				min = b
			}
		}
		if(sum(trendsdataDAILY[b:max,]) > 0)
		{
			counter = counter + 1
			top = newXMLNode("top") # las interval 
			newXMLNode("num",paste("Number : ", counter),parent = top)
			newXMLNode("title",paste(query,"@",b,"_",max,sep=""),parent = top)
			newXMLNode("desc",paste("Description : ", counter),parent = top)
			from = querydata[querydata$id==round(b/REPEAT),]$week
			to = querydata[querydata$id==round(max/REPEAT),]$week
			newXMLNode("narr", paste("from", from, "to" ,to ),parent = top)
			write(toString.XMLNode(top),PAHTOSAVE,append=TRUE)
		}
	}
 
#ts = ts(t[index,],start=1,frequency=1)
#plot(ts,title=query)
#abline(v=breaks,col="red")
}
