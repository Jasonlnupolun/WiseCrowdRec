WiseCrowdRec
============

- [x] Streaming processing   
    - [x] [Storm](https://github.com/faustineinsun/WiseCrowdRec/tree/master/WiseCrowdRec/src/main/java/com/feiyu/storm/streamingdatacollection)       
    - [x] [Spark Streaming (Apache Spark 0.9.1)](https://github.com/faustineinsun/WiseCrowdRec/tree/master/WiseCrowdRec/src/main/java/com/feiyu/spark)        
- [x] Deap learning  
    - [x] [Restricted Boltzmann Machines (RBM) for Collaborative Filtering](https://github.com/faustineinsun/WiseCrowdRec/tree/master/WiseCrowdRec/src/main/java/com/feiyu/deeplearning/RBM)        
- [x] Machine learning lib 
    - [x] [Mahout on Hadoop (Collaborative Filtering)](https://github.com/faustineinsun/MahoutHadoopUseCase)           
- [x] [nlp](https://github.com/faustineinsun/WiseCrowdRec/tree/master/WiseCrowdRec/src/main/java/com/feiyu/nlp)    
    - [x] Stanford CoreNLP    
    - [x] Calais      
- [ ] Multithreaded, Parallel, and Distributed    
    - [x] Multithreading (in Java)    
    - [x] Parallel      
    - [ ] Distributed      
- [x] [WebSocket](https://github.com/faustineinsun/WiseCrowdRec/tree/master/WiseCrowdRec/src/main/java/com/feiyu/websocket)    
- [x] [Rabbitmq](https://github.com/faustineinsun/WiseCrowdRec/search?p=1&q=rabbitmq&utf8=%E2%9C%93)        
- [x] [ActiveMQ](https://github.com/faustineinsun/WiseCrowdRec/tree/master/WiseCrowdRec/src/main/java/com/feiyu/storm/streamingdatacollection/stormmsg2websockets)    
- [x] [Apache Camel](https://github.com/faustineinsun/WiseCrowdRec/blob/master/WiseCrowdRec/src/main/resources/SpringApplicationContext.xml) -> from(“file://xxxx").to("activemq://xxxx") (to ActiveMQ's JMS)  
    - $ `netstat -a` -> to check if the activemq process has started    
    - $ `bin/activemq start`   
    - $ `lsof -i:<port>`    
    - $ `kill -9 {PID}`    
    - $ `sudo lsof -i -n -P | grep TCP`  
- [x] [Storm JMS](https://github.com/ptgoetz/storm-jms) -> Java Message Service (JMS)      
- [x] [Freebase](https://github.com/faustineinsun/WiseCrowdRec/blob/master/WiseCrowdRec/src/main/java/com/feiyu/semanticweb/freebase)    
- [x] [Twitter4J](https://github.com/faustineinsun/WiseCrowdRec/blob/master/WiseCrowdRec/src/main/java/com/feiyu/semanticweb/freebase)    
- [x] [Sign in with Twitter](https://github.com/faustineinsun/WiseCrowdRec/blob/c2eb79b360ade0aae0b9b44b6c54221110ad05d9/WiseCrowdRec/src/main/java/com/feiyu/springmvc/controller/TweetsAnalyzerController.java)    
- [x] [nosql - Cassandra](https://github.com/faustineinsun/WiseCrowdRec/tree/master/WiseCrowdRec/src/main/java/com/feiyu/Cassandra)     
- [x] [data search - Elasticsearch](https://github.com/faustineinsun/WiseCrowdRec/tree/master/WiseCrowdRec/src/main/java/com/feiyu/elasticsearch)     
- [x] Hadoop 2.2.0   
- [x] [Tomcat 7](https://github.com/faustineinsun/WiseCrowdRec/tree/master/bin)      
- [x] [Spring MVC](https://github.com/faustineinsun/WiseCrowdRec/tree/master/WiseCrowdRec/src/main/java/com/feiyu/springmvc)    
- [x] REST API  
- [x] Google Compute Engine  
- [x] Google Web Toolkit (GWT)  
- [x] [D3.js Force-Directed Graph](https://github.com/faustineinsun/WiseCrowdRec/tree/master/WiseCrowdRec/src/main/webapp/resources/js/wisecrowdrec)    
- [x] Jetty  
- [ ] kafka  
- [ ] Akka  
- [ ] Zookeeper  
- [ ] Ajax  
- [ ] node.js  
- [x] [Scripts for running this project automatically](https://github.com/faustineinsun/WiseCrowdRec/tree/master/scripts)  
- [x] [Unit Test](https://github.com/faustineinsun/WiseCrowdRec/tree/master/WiseCrowdRec/src/test/java/com/feiyu)  
- [x] [log4j](https://github.com/faustineinsun/WiseCrowdRec/blob/master/WiseCrowdRec/src/main/resources/log4j.properties)    
- [x] [Maven](https://github.com/faustineinsun/WiseCrowdRec/blob/master/WiseCrowdRec/pom.xml)    
- [x] [WebApp](https://github.com/faustineinsun/WiseCrowdRec/tree/c2eb79b360ade0aae0b9b44b6c54221110ad05d9/WiseCrowdRec/src/main/webapp)    

To do list:  
- [ ] Cassandra - asynchronous -> Astyanax feature      
- [ ] kill storm topology  
- [ ] Log  
- [ ] Click "Start Background Topology", if cassandra doesn't open, show info  
- [ ] Throw exception -> use Exception only        
- [ ] local mode + distributive mode   
 


--- 
[inspect WebSocket messages](http://www.websocket.org/echo.html)-> ws://0.0.0.0:9292/wcrstorm     

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

[http://localhost:9999/](http://localhost:9999/)      
[http://localhost:9999/docs/](http://localhost:9999/docs/)    
[http://localhost:9999/examples/](http://localhost:9999/examples/)      
[http://localhost:9999/manager/html](http://localhost:9999/manager/html)      
[http://localhost:9999/host-manager/html](http://localhost:9999/host-manager/html)     

- Before importing this Maven project into eclipse, run $ `mvn eclipse:eclipse`      

---

[GCE](https://cloud.google.com/products/compute-engine/)      

- $ `sudo su`    
- $ `source ~/.bash_profile`  

[http://173.255.114.111:9999/](http://173.255.114.111:9999/)    
[http://173.255.114.111:9999/docs/](http://173.255.114.111:9999/docs/)    
[http://173.255.114.111:9999/examples/](http://173.255.114.111:9999/examples/)    
[http://173.255.114.111:9999/manager/html](http://173.255.114.111:9999/manager/html)    
[http://173.255.114.111:9999/host-manager/html](http://173.255.114.111:9999/host-manager/html)    

~~sudo service tomcat7 start~~    
~~sudo service tomcat7 restart~~    
~~sudo service tomcat7 stop~~      

---

[Git](http://www.vogella.com/tutorials/Git/article.html)  
mvn versions:display-plugin-updates      
mvn eclipse:clean eclipse:eclipse -Dwtpversion=2.0    
  
---

Eclipse VM: -XX:MaxPermSize=1024M -Xms1024m -Xmx1024m -Dlog4j.debug=true  

