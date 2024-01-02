package org.msse.attachschema.streams;

import java.util.regex.Pattern;

public class Options  {

    private Pattern in;

    private String outTopic;

    public Pattern in() {
        return in;
    }

    public void in(String in) {
        System.out.println("!!!!!! " + in);
        this.in = Pattern.compile(in);
    }

    public String outTopic() {
        return outTopic;
    }

    public void outTopic(String outTopic) {
        this.outTopic = outTopic;
    }
}
