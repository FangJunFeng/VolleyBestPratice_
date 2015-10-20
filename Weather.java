package com.alandy.volleybestpratice;

/**
 * Created by AlandyFeng on 2015/10/19.
 */
public class Weather {
    /**
     * {"weatherinfo":{"city":"北京","cityid":"101010100","temp":"19","WD":"南风","WS":"2级",
     * "SD":"43%","WSE":"2","time":"19:45","isRadar":"1","Radar":"JC_RADAR_AZ9010_JB"}}
     */
    //weatherinfo必须要和json串中一致，否则在服务器端拿不到数据
    //服务器返回的json数据名字是weatherinfo，gson是根据这个名字到类里面去查找响应字段的，如果改了就找不到了。
    private WeatherInfo weatherinfo;

    public WeatherInfo getWeatherinfo() {
        return weatherinfo;
    }

    public void setWeatherinfo(WeatherInfo weatherinfo) {
        this.weatherinfo = weatherinfo;
    }
}
