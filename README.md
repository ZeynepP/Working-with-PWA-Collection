Working-with-PWA-Collection
===========================

Portuguese web archive (PWA) test collection was created to support research
on web archive information retrieval (WAIR) and it is composed by six crawls
of the Portuguese web [1]. It contains 256 million documents obtained from
different crawls, corresponding to 6.2 TB of compressed data in ARC format
(8.9 TB uncompressed). 


## Requirements

* Java 
* Python
* R
* Lucene > 4.0.0
* Maven


## Using the project

It consists of three steps :

1. Getting: It is a python script that downloads PWA4 collection from Internet Archive.
2. Indexing: It indexes PWA collection obtained in step 1 by using Lucene (Java, maven, lucene).
3. Temporal_Queries: It uses Google trends to generate temporal queries (R, Python). More detail in ./Temporal_Queries/README.md


## References

[1] Costa, M., Silva, M. J., 2012. Evaluating web archive search systems. In:
Wang, X. S., Cruz, I. F., Delis, A., Huang, G. (Eds.), WISE. Vol. 7651 of
Lecture Notes in Computer Science. Springer.
URL http://dblp.uni-trier.de/db/conf/wise/wise2012.htmlCostaS12


