/**
 * @author feiyu
 * https://code.google.com/p/j-calais/
 * http://www.opencalais.com/APIkey
 * Bottleneck: 
 *    1) Server returned HTTP response code: 403 for URL: http://api.opencalais.com/enlighten/rest/ 
 *    2) Calais continues to expand its list of supported languages, but does not yet support your submitted content.
 * -> use stanford corenlp instead
 */
package com.feiyu.nlp;

import java.io.IOException;
import java.util.HashMap;

import com.feiyu.springmvc.model.Tweet;
import com.feiyu.utils.GlobalVariables;

import mx.bigdata.jcalais.CalaisObject;
import mx.bigdata.jcalais.CalaisResponse;

public class EntityExtractionCalais {
  private Tweet _t = new Tweet();
  private HashMap<String, String> _hm = new HashMap<String, String>(); 

  private void getCleanedText(String text) {
    _t.setText(text.replaceAll("(?:^|\\s)[a-zA-Z0-9]+(?=\\s|$)"," "));//("[^a-zA-Z0-9]", " "));
    // This will match all words containing the letters A-Z.
    // (?:^|\s)[a-zA-Z]+(?=\s|$)
  }

  public HashMap<String, String> getEntities(String text) throws IOException {
    //		CalaisClient client = new CalaisRestClient(GlobalVariables.WCR_PROPS.getProperty("CalaisApiKey"));
    this.getCleanedText(text);
    CalaisResponse response = GlobalVariables.CALAIS_CLIENT.analyze(_t.getText());
    for (CalaisObject entity : response.getEntities()) {
      if (entity.getField("_type").equals("Person")
          || entity.getField("_type").equals("Organization")
          || entity.getField("_type").equals("Company")
          || entity.getField("_type").equals("Country")
          || entity.getField("_type").equals("City")
          || entity.getField("_type").equals("categoryName")) {
        _hm.put(entity.getField("name"), entity.getField("_type"));
      }
    }
    return _hm;
  }
}
