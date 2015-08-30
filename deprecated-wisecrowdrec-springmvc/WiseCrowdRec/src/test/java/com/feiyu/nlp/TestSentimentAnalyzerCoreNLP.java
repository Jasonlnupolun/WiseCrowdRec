package com.feiyu.nlp;
/**
 * @author feiyu
 */

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.feiyu.springmvc.model.EntityWithSentiment;
import com.feiyu.utils.InitializeWCR;

public class TestSentimentAnalyzerCoreNLP {
  private static Logger log = Logger.getLogger(TestSentimentAnalyzerCoreNLP.class.getName());

  @Test
  public void testGetEntitiesWithSentiment() {
    InitializeWCR initWcr = new InitializeWCR();
    initWcr.coreNLPInitial();

    SentimentAnalyzerCoreNLP sacn = new SentimentAnalyzerCoreNLP();

    String text = "Back on form. That ending = awesome! - I rated X-Men: Days of Future Past 7/10  #IMDb http://www.imdb.com/title/tt1877832";
    //		String text = "Nicholas Cage in San Francisco, this is great! Jack Bauer in Los Angeles, this is bad!";
    EntityWithSentiment ews = sacn.getEntitiesWithSentiment(text);
    log.info(ews.toString());
    Iterator<Entry<String, String>> it = ews.getEntityWithCategory().entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, String> pairs = it.next();
      log.info(pairs.getKey() + " -> " + pairs.getValue());
      it.remove(); // avoids a ConcurrentModificationException
    }
  }
}
