package domains.Weather;

import backend.*;
import nlp.MatchedSequence;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class FindWeather extends Domain {
    public FindWeather() {
        super(DomainNames.FindWeather);
        addPattern("<weather> <in, at> <...>");

    }

    /*
    Assuming (for now) the weather Query always looks like : "Weather in <City>"
    So all words after "in" make up a name of a city
     */

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        String city = sequence.getStringAt(2);
        String str = "The weather in " + city + " is ";
        return new Skill(this, outputChannel) {
            @Override
            public void run() {

                if(!city.isEmpty()){
                    String weather = null;
                    try {
                        weather = CurrentWeather.getWeather(city);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(weather!=""){
                        pushMessage(str+weather+"'C", MessageType.STRING);
                        System.out.println(str+weather+"'C");
                    }else{
                        pushMessage("Can't find the weather in " + city, MessageType.STRING);
                        System.out.println("Can't find the weather in " + city);

                    }
                }else{
                    pushMessage("Please enter a city name", MessageType.STRING);
                    System.out.println("Please enter a city name");
                }

            }
        };
    }
}
