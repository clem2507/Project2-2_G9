package domains.Weather;

import backend.AssistantMessage;
import backend.Domain;
import backend.DomainNames;
import backend.Skill;
import nlp.MatchedSequence;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class FindWeather extends Domain {
    public FindWeather() {
        super(DomainNames.FindWeather);
    }

    /*
    Assuming (for now) the weather Query always looks like : "Weather in <City>"
    So all words after "in" make up a name of a city
     */

    /*public Skill dispatchSkill(List<String> tokens, BlockingQueue<AssistantMessage> resultsQueue) {
        return new Skill(this, tokens, resultsQueue) {
            @Override
            public void run() {

                //TODO: This is inefficient and should be modified.
                String city = "";
                int in = tokens.indexOf("in");
                for (int i = in+1 ; i <tokens.size(); i++){
                    city = city + tokens.get(i) + " ";
                }

                String temp = CurrentWeather.getWeather(city);
                String message = "Weather in " + city + " is " + temp + "C";

                pushMessage(message);
                System.out.println(message);
            }
        };
    }*/

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> resultsQueue) {
        return null;
    }
}
