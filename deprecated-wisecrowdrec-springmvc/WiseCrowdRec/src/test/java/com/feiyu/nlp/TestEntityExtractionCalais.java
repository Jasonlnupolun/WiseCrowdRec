package com.feiyu.nlp;
/**
 * @author feiyu
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;

public class TestEntityExtractionCalais {
  @Before
  public void init() throws IOException {
    GlobalVariables.WCR_PROPS = new Properties();
    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
    GlobalVariables.WCR_PROPS.load(in);
    InitializeWCR initWCR = new InitializeWCR();
    initWCR.calaisNLPInitial();
  }

  @Test
  public void testGetEntities() throws IOException {
    EntityExtractionCalais entityExtract = new EntityExtractionCalais();
    HashMap<String, String> hm = new HashMap<String, String>();
    hm = entityExtract.getEntities("Nicholas Cage is great!");
    Iterator<Entry<String, String>> it = hm.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, String> pairs = it.next();
      String entity =  pairs.getKey();
      String category =  pairs.getValue();
      it.remove(); // avoids a ConcurrentModificationException
      System.out.println("Entity:"+entity+", Category:"+category);
    }
  }

}
