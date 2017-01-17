package cc.holstr.wfu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Merchant {

@SerializedName("name")
@Expose
private String name;
@SerializedName("number")
@Expose
private String number;

public Merchant(String name, String number) {
	this.name = name;
	this.number = number;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public Merchant withName(String name) {
this.name = name;
return this;
}

public String getNumber() {
return number;
}

public void setNumber(String number) {
this.number = number;
}

public Merchant withNumber(String number) {
this.number = number;
return this;
}

	@Override
	public String toString() {
		return "Merchant{" +
				"name='" + name + '\'' +
				", number='" + number + '\'' +
				'}';
	}
}