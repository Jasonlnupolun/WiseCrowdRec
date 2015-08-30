package com.feiyu.nlp;
/**
 * reference: 
 * http://nlp.stanford.edu/software/corenlp.shtml#Usage
 * https://github.com/drewfarris/corenlp-examples/blob/master/src/main/java/drew/corenlp/SimpleExample.java
 * @author feiyu
 */

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.feiyu.classes.EntityWithSentiment;
import com.feiyu.utils.GlobalVariables;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.AnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentAnalyzerCoreNLP {
  private static Logger log = Logger.getLogger(SentimentAnalyzerCoreNLP.class.getName());

  //	public void initCoreNLP() { props = new Properties();
  //		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
  //		pipeline = new StanfordCoreNLP(props);
  //	}

  public EntityWithSentiment getEntitiesWithSentiment(String tweetText) {
    EntityWithSentiment ews = new EntityWithSentiment();

    if (tweetText != null && tweetText.length() > 0) {
      Annotation annotation= new Annotation(tweetText);
      GlobalVariables.CORENLP_PIPELINE.annotate(annotation);

      int textSentiment = 0;
      HashMap<String, String> entityWithCategory = new HashMap<String, String>();

      List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
      for (CoreMap sentence : sentences) {

        Tree sentimentTree = sentence.get(AnnotatedTree.class);
        int sentiment = RNNCoreAnnotations.getPredictedClass(sentimentTree);
        textSentiment += sentiment;

        log.debug("==========="+sentiment);

        List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
        String previousLabel ="" , entity = "";
        for (CoreLabel token : tokens) {
          String nerLabel = token.get(NamedEntityTagAnnotation.class);
          //					System.out.println(">>>>>>> "+nerLabel);
          // StringUtils.equals is null safe
          if (!StringUtils.equals(nerLabel, "O")) {
            String word = token.get(TextAnnotation.class);

            if (entity.length()>0 && (!StringUtils.equals(nerLabel, previousLabel))) {
              log.debug(">>>>> entity: "+entity.trim());
              log.debug(">>>>> type: "+previousLabel);
              entityWithCategory.put(entity.trim(), previousLabel);

              entity = word + " ";
            }  else {
              entity += word + " ";
            }
            previousLabel = nerLabel;
          }
        }
        log.debug(">>>>>>> entity: "+entity.trim());
        log.debug(">>>>>>> type: "+previousLabel);
        entityWithCategory.put(entity.trim(), previousLabel);

      }
      log.debug("++++++++++++++++++++++ " + sentences.size());
      if (sentences.size() > 0) {
        textSentiment /= sentences.size(); 
        // @@@ average sentiment of multiple sentences, what is a better solution
        // http://stackoverflow.com/questions/21999067/how-to-get-overall-sentiment-for-multiple-sentences/22006491#22006491
        log.debug("---------- textSentiment"+ textSentiment);
        ews.setSentiment(textSentiment);
        ews.setEntityWithCategory(entityWithCategory);
      }
    }
    return ews;
  }
}