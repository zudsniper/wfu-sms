package cc.holstr.wfu.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StaticInfo {

	@SerializedName("welcome")
	@Expose
	private String welcome;
	@SerializedName("stock-spreadsheet-id")
	@Expose
	private String stockSpreadsheetId;
	@SerializedName("stock-users")
	@Expose
	private List<String> stockUsers = null;
	@SerializedName("stats-spreadsheet-id")
	@Expose
	private String statsSpreadsheetId;
	@SerializedName("stats-users")
	@Expose
	private List<String> statsUsers = null;
	@SerializedName("locations")
	@Expose
	private List<String> locations = null;
	@SerializedName("times")
	@Expose
	private List<String> times = null;
	@SerializedName("merchants")
	@Expose
	private List<Merchant> merchants = null;

	public String getWelcome() {
		return welcome;
	}

	public void setWelcome(String welcome) {
		this.welcome = welcome;
	}

	public StaticInfo withWelcome(String welcome) {
		this.welcome = welcome;
		return this;
	}

	public String getStockSpreadsheetId() {
		return stockSpreadsheetId;
	}

	public void setStockSpreadsheetId(String stockSpreadsheetId) {
		this.stockSpreadsheetId = stockSpreadsheetId;
	}

	public StaticInfo withSpreadsheetId(String spreadsheetId) {
		this.stockSpreadsheetId = spreadsheetId;
		return this;
	}

	public List<String> getStockUsers() {
		return stockUsers;
	}

	public void setStockUsers(List<String> stockUsers) {
		this.stockUsers = stockUsers;
	}

	public StaticInfo withStockUsers(List<String> stockUsers) {
		this.stockUsers = stockUsers;
		return this;
	}

	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(List<String> locations) {
		this.locations = locations;
	}

	public StaticInfo withLocations(List<String> locations) {
		this.locations = locations;
		return this;
	}

	public List<String> getTimes() {
		return times;
	}

	public void setTimes(List<String> times) {
		this.times = times;
	}

	public StaticInfo withTimes(List<String> times) {
		this.times = times;
		return this;
	}

	public List<Merchant> getMerchants() {
		return merchants;
	}

	public void setMerchants(List<Merchant> merchants) {
		this.merchants = merchants;
	}

	public StaticInfo withMerchants(List<Merchant> merchants) {
		this.merchants = merchants;
		return this;
	}

	public String getStatsSpreadsheetId() {
		return statsSpreadsheetId;
	}

	public List<String> getStatsUsers() {
		return statsUsers;
	}
}