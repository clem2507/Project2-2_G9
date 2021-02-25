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

        addPattern("<humidity> <in, at> <...>");

        addPattern("<feel like, feels like> <in, at> <...>");

        addPattern("<min temperature, minimum temp, minimum temperature, min temp> <in, at> <...>");

        addPattern("<max temperature, maximum temp, maximum temperature, max temp> <in, at> <...>");

        addPattern("<temperature, temp> <in, at> <...>");

        addPattern("<visibility> <in, at> <...>");

        addPattern("<wind speed, wind> <in, at> <...>");
        addPattern("<how windy is it in> <...>");
    }

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        String city = sequence.getStringAt(2);
        return new Skill(this, outputChannel) {
            @Override
            public void run() {

                if(!city.isEmpty()){
                    String temp = null;
                    WeatherObject currentWeather = null;

                    try {
                        currentWeather = new WeatherObject(city);
                        temp = currentWeather.getTemp();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String matched = sequence.getStringAt(0).toLowerCase();

                    if(currentWeather!=null){
                        if(matched.contains("weather")){

                            pushMessage("Current Temperature = "+currentWeather.getTemp() + "'C", MessageType.STRING);
                            pushMessage("Feels Like = "+currentWeather.getFeelsLike()+ "'C", MessageType.STRING);
                            pushMessage("Maximum Temperature = "+currentWeather.getMaxTemp()+ "'C", MessageType.STRING);
                            pushMessage("Minimum Temperature = "+currentWeather.getMinTemp()+ "'C", MessageType.STRING);
                            pushMessage("Humidity = "+currentWeather.getHumidity(), MessageType.STRING);
                            pushMessage("Wind Speed = "+currentWeather.getWindSpeed(), MessageType.STRING);
                            pushMessage("Visibility = "+currentWeather.getVisibility(), MessageType.STRING);


                        }else if (matched.contains("humidity")){
                            pushMessage("Humidity = "+currentWeather.getHumidity(), MessageType.STRING);
                        }else if (matched.contains("feel")){
                            pushMessage("Feels Like = "+currentWeather.getFeelsLike()+ "'C", MessageType.STRING);
                        }else if (matched.contains("max")){
                            pushMessage("Maximum Temperature = "+currentWeather.getMaxTemp()+ "'C", MessageType.STRING);
                        }else if(matched.contains("min")){
                            pushMessage("Minimum Temperature = "+currentWeather.getMinTemp()+ "'C", MessageType.STRING);
                        }else if (matched.contains("wind")){
                            pushMessage("Wind Speed = "+currentWeather.getWindSpeed(), MessageType.STRING);
                        }else if(matched.contains("visibility")){
                            pushMessage("Visibility = "+currentWeather.getVisibility(), MessageType.STRING);
                        }else {
                            pushMessage("Current Temperature = "+currentWeather.getTemp()+ "'C", MessageType.STRING);
                        }
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
