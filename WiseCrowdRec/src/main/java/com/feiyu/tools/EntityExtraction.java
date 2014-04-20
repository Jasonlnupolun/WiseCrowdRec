/**
 * @author feiyu
 * https://code.google.com/p/j-calais/
 * http://www.opencalais.com/APIkey
 */
package com.feiyu.tools;

import java.io.IOException;
import java.util.HashMap;

import com.feiyu.model.Tweet;

import mx.bigdata.jcalais.CalaisClient;
import mx.bigdata.jcalais.CalaisObject;
import mx.bigdata.jcalais.CalaisResponse;
import mx.bigdata.jcalais.rest.CalaisRestClient;

public class EntityExtraction {
	private Tweet _t = new Tweet();
	private HashMap<String, String> _hm = new HashMap<String, String>(); 
	
	private void getCleanedText(String text) {
		_t.setText(text.replaceAll("[^a-zA-Z0-9]", " "));
		// This will match all words containing the letters A-Z.
		// (?:^|\s)[a-zA-Z]+(?=\s|$)
	}
	
	public HashMap<String, String> getEntities(String text) throws IOException {
		CalaisClient client = new CalaisRestClient("vtxfm9syum9mhxn4yj8x5fck");
		this.getCleanedText(text);
		CalaisResponse response = client.analyze(_t.getText());
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
