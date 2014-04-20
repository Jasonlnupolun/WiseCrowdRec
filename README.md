WiseCrowdRec
============

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

GCE  

- $ `sudo su`    

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
  

