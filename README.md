WiseCrowdRec
============

- [x] Streaming processing - Storm   
- [x] nosql - Cassandra   
- [ ] data search - Elasticsearch   
- [ ] Zookeeper  
- [x] Hadoop 2.2.0   
- [x] Machine learning lib - Mahout  
- [x] nlp  
    - [x] core NLP  
    - [x] Calais      
- [x] Tomcat  
- [x] Spring MVC  
- [ ] Ajax  
- [x] Google Compute Engine  
- [x] Google Web Toolkit (GWT)  
- [ ] D3 Force-Directed Graph  
- [ ] Apache Spark 0.9.1  

To do list:  
- [ ] Cassandra - asynchronous -> Astyanax feature      
- [ ] Allow users access their twitter info (oauth)     
    - since when multiple user search twitter, they should use their own oauth  
- [ ] kill storm topology  
- [ ] Log  
- [ ] Click "Start Background Topology", if cassandra doesn't open, show info  
- [ ] Throw exception -> use Exception only        


--- 

[Shapeshifter](https://github.com/turn/shapeshifter): Protocol Buffers -> JSON    
[JSON -> JavaScript](http://www.mkyong.com/javascript/how-to-access-json-object-in-javascript/)  

---

Elasticsearch

- $ ./bin/elasticsearch -d  
- $ curl -XGET localhost:9200/wcresidx/_search?  
- $ curl -XDELETE 'http://localhost:9200/wcresidx/'  
- $ curl http://localhost:9200/_aliases  (list all indexes)    

---

- Cassandra   
    - ~~Pelops,Hector~~ -> [Astyanax](https://github.com/Netflix/astyanax): A Java client library for the Cassandra database    
    - Open Cassandra -> $ bin/cassandra -f  
    - Open CQL3 -> $ bin/cqlsh   
    - chmod +x schemaCassandra.txt  
    - Put schema into cassandra -> $ cat schemaCassandra.txt | bin/cassandra-cli -h localhost  
    - run java code  
    - $ bin/cassandra-cli -h localhost  
        - [default@unknown] use wcrkeyspace;  
        - [default@unknown] DESCRIBE wcrkeyspace;   
        - [default@unknown] list backgroundsearch;   
        - [default@unknown] list dynamicsearch;   

"[NOTE: Transactional topologies have been deprecated -- use the Trident framework instead.](https://github.com/nathanmarz/storm/wiki/Transactional-topologies)"  
[Trident tutorial](https://github.com/nathanmarz/storm/wiki/Trident-tutorial)    

---

Setup project path in system bash file, for example, in Mac OS:  
- $ `vim ~/.bash_profile`    

```
# WiseCrowdRec project home
export WISECROWDREC_HOME=/Users/workspace/WiseCrowdRec/
export PATH=$PATH:$WISECROWDREC_HOME/bin
```

- $ `source ~/.bash_profile`    

---

- Create the file named `config.properties` in `src/main/resources`  
- Put your [twitter app](https://apps.twitter.com/) OAuth and WiseCrowdRec config info in `config.properties` as follows:   

```
debug=true
oauth.consumerKey=********
oauth.consumerSecret=********
oauth.accessToken=********
oauth.accessTokenSecret=********

SEARCH_PHRASES=movie
```

---

- `start_WiseCrowdRec.sh`    
- `deployWCRonTomcat.sh`  
- `startStreamCollection.sh`  
- `beforeCommit.sh`  

---

both on local and GCE    

- /Library/Tomcat/bin/startup.sh    
- /Library/Tomcat/bin/shutdown.sh     
- $ `mvn clean package tomcat7:deploy`    
- $ `mvn tomcat7:undeploy`    


---

local     

- $ `gcloud auth login`    
- $ `gcutil listinstances`    
- get `ssh` link from gce client website, and then use this link connect remote machine    

[http://localhost:8080/](http://localhost:8080/)      
[http://localhost:8080/docs/](http://localhost:8080/docs/)    
[http://localhost:8080/examples/](http://localhost:8080/examples/)      
[http://localhost:8080/manager/html](http://localhost:8080/manager/html)      
[http://localhost:8080/host-manager/html](http://localhost:8080/host-manager/html)     

- Before importing this Maven project into eclipse, run $ `mvn eclipse:eclipse`      

---

[GCE](https://cloud.google.com/products/compute-engine/)      

- $ `sudo su`    
- $ `source ~/.bash_profile`  

[http://173.255.114.111:8080/](http://173.255.114.111:8080/)    
[http://173.255.114.111:8080/docs/](http://173.255.114.111:8080/docs/)    
[http://173.255.114.111:8080/examples/](http://173.255.114.111:8080/examples/)    
[http://173.255.114.111:8080/manager/html](http://173.255.114.111:8080/manager/html)    
[http://173.255.114.111:8080/host-manager/html](http://173.255.114.111:8080/host-manager/html)    

~~sudo service tomcat7 start~~    
~~sudo service tomcat7 restart~~    
~~sudo service tomcat7 stop~~      

---

[Git](http://www.vogella.com/tutorials/Git/article.html)  
mvn versions:display-plugin-updates      
mvn eclipse:clean eclipse:eclipse -Dwtpversion=2.0    
  

