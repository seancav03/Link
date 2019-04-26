package org.headroyce.sean.link;

/**
 * Created by sean on 12/19/18.
 */

public class keyAndValue {

    private String key;
    private String value;

    keyAndValue(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey(){ return key; }
    public String getValue() { return value; }

}
