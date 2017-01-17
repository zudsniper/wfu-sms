package cc.holstr.wfu.model.pickup;

import cc.holstr.wfu.properties.Unpacker;

import java.util.List;
import java.util.Set;

/**
 * Created by jason on 1/8/17.
 */
public class TimeAndPlace {

	public static List<String> validTimes;
	public static List<String> validPlaces;

	private String time;
	private String place;

	private TimeAndPlace(String time, String place) {
		this.time = time;
		this.place = place;
	}

	public String getTime() {
		return time;
	}

	private void setTime(String time) {
		this.time = time;
	}

	public String getPlace() {
		return place;
	}

	private void setPlace(String place) {
		this.place = place;
	}

	public static TimeAndPlace begin() {
		return new TimeAndPlace("","");
	}

	public static TimeAndPlace create(String time, String place) {
		if(validTimes==null)
			validTimes = Unpacker.STATIC_INFO.getTimes();
		if(validPlaces==null)
			validPlaces = Unpacker.STATIC_INFO.getLocations();

		if(validTimes.contains(time) && validPlaces.contains(place))
			return new TimeAndPlace(time, place);
		else
			return null;

	}

	public static String list() {
		if(validTimes==null)
			validTimes = Unpacker.STATIC_INFO.getTimes();
		if(validPlaces==null)
			validPlaces = Unpacker.STATIC_INFO.getLocations();

		String list = "";
		for(String place : validPlaces) {
			list+= place + "   \n";
			for(String time : validTimes) {
				list+= time + " ";
			}
			list+= "\n";
		}

		return list;
	}

	@Override
	public String toString() {
		return "TimeAndPlace{" +
				"time='" + time + '\'' +
				", place='" + place + '\'' +
				'}';
	}
}
