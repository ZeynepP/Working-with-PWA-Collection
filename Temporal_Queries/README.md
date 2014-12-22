# Temporal queries for PWA Collection

I needed temporal queries like query@T1_T2 ( query from T1 to T2). 
The basic idea is to use Google Trends to obtain time series for each query from query logs of PWA. Then, temporal queries can be obtained
by segmenting these time series data to find different time intervals where users were interested in.
For each query, we set geo=PORT and the time interval to the collection's time interval. Here is an example of Google trends page 
for query "cavaco silva". 

![Alt text](https://github.com/ZeynepP/Working-with-PWA-Collection/blob/master/Temporal_Queries/Selection_001.png "Example of Google Trends ")

## Using Google Trends

However, extrating data from google trends is not an easy task because of famous "you reached your quota limit". Google lets you send just a few automatic request per day. There are several solutions out there but it seems that they work for a while and then google changes the interface and then the developers propose a new version and then google changes again the interface...

Here are the list of solutions that I tried and it did not work

* https://github.com/elibus/j-google-trends-api
* Using selenium to get the snapshot of the page and then parse the pages
* Changing my IP (in R) for each request
* and more ...

I ended up by using quasi-automated way by using two google extensions:

* [Chromium scrapbook](https://chrome.google.com/webstore/detail/chromium-scrapbook/) : It saves a web page for an offline use. It uses SQLLite.

* [OpenList](https://github.com/cdzombak/OpenList): It is a chrome extension to open a list of URLs 



The syntaxof the url to use is as follows:
```
http://www.google.com/trends/fetchComponent?q=YOUR_QUERY_HERE&cid=TIMESERIES_GRAPH_0&export=3
```
That returns a runnable piece of code, as shown below, that sets some variables (which you can synthesize if need be!) to the values of the graph displayed on the trends.google.com search page! Thanks to the discussion on [stackoverflow](http://stackoverflow.com/questions/7805711/javascript-json-google-trends-api)


```
https://www.google.com/trends/fetchComponent?geo=PT&date=1%2F2008%2024m&cmpt=date&cid=TIMESERIES_GRAPH_0&export=3&q=jose%20socrates

// Data table response
google.visualization.Query.setResponse({"version":"0.6","status":"ok","sig":"645515248","table":{"cols":[{"id":"date","label":"Date","type":"date","pattern":""},{"id":"query0","label":"2008-2009","type":"number","pattern":""}],"rows":[{"c":[{"v":new Date(2008,0,1),"f":"January 2008"},{"v":20.0,"f":"20"}]},{"c":[{"v":new Date(2008,1,1),"f":"February 2008"},{"v":16.0,"f":"16"}]},{"c":[{"v":new Date(2008,2,1),"f":"March 2008"},{"v":17.0,"f":"17"}]},{"c":[{"v":new Date(2008,3,1),"f":"April 2008"},{"v":9.0,"f":"9"}]},{"c":[{"v":new Date(2008,4,1),"f":"May 2008"},{"v":9.0,"f":"9"}]},{"c":[{"v":new Date(2008,5,1),"f":"June 2008"},{"v":10.0,"f":"10"}]},{"c":[{"v":new Date(2008,6,1),"f":"July 2008"},{"v":18.0,"f":"18"}]},{"c":[{"v":new Date(2008,7,1),"f":"August 2008"},{"v":12.0,"f":"12"}]},{"c":[{"v":new Date(2008,8,1),"f":"September 2008"},{"v":12.0,"f":"12"}]},{"c":[{"v":new Date(2008,9,1),"f":"October 2008"},{"v":12.0,"f":"12"}]},{"c":[{"v":new Date(2008,10,1),"f":"November 2008"},{"v":12.0,"f":"12"}]},{"c":[{"v":new Date(2008,11,1),"f":"December 2008"},{"v":19.0,"f":"19"}]},{"c":[{"v":new Date(2009,0,1),"f":"January 2009"},{"v":36.0,"f":"36"}]},{"c":[{"v":new Date(2009,1,1),"f":"February 2009"},{"v":37.0,"f":"37"}]},{"c":[{"v":new Date(2009,2,1),"f":"March 2009"},{"v":26.0,"f":"26"}]},{"c":[{"v":new Date(2009,3,1),"f":"April 2009"},{"v":22.0,"f":"22"}]},{"c":[{"v":new Date(2009,4,1),"f":"May 2009"},{"v":19.0,"f":"19"}]},{"c":[{"v":new Date(2009,5,1),"f":"June 2009"},{"v":15.0,"f":"15"}]},{"c":[{"v":new Date(2009,6,1),"f":"July 2009"},{"v":17.0,"f":"17"}]},{"c":[{"v":new Date(2009,7,1),"f":"August 2009"},{"v":24.0,"f":"24"}]},{"c":[{"v":new Date(2009,8,1),"f":"September 2009"},{"v":100.0,"f":"100"}]},{"c":[{"v":new Date(2009,9,1),"f":"October 2009"},{"v":32.0,"f":"32"}]},{"c":[{"v":new Date(2009,10,1),"f":"November 2009"},{"v":23.0,"f":"23"}]},{"c":[{"v":new Date(2009,11,1),"f":"December 2009"},{"v":24.0,"f":"24"}]}]}});
```


I generetated list of urls and copy-paste OpenList as a bunch of 200 links. Then, jsut click the button "save all open tabs" of scrapbook. 
After that operation around for 3000 queries, I was not even able to access to my gmail account for a while but it worthed it. I got the data for my research!!!

## Generating temporal queries

* ParseSQLLite.py is used to parse google trends results and to add results into Sqllite db.
* TrendsTemporalQueriesDaily.R segments time series obtained from google trends by using bfast R package. It expends time series to daily time series as google trends returns daily, weekly or monthly results. The output is a TREC style topic file. (PWATopicsDaily.xml)






