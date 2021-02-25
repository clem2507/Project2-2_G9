package domains;

import backend.*;
import backend.common.CurrentWeather;
import backend.common.WeatherObject;
import nlp.MatchedSequence;

import java.io.IOException;
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
                    String temp = null;
                    try {
                        WeatherObject currentWeather = new WeatherObject(city);
                        temp = currentWeather.getTemp();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    if(!temp.equals("")){
                        pushMessage(str+temp+"'C", MessageType.STRING);
                        System.out.println(str+temp+"'C");
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
