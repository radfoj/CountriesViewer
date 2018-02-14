package cz.fojtik.radek.countryviewer.api;

/**
 * Created by Radek on 21. 1. 2018.
 */
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

public class ResponseData {
    @SerializedName("population")
    private String population;

    @SerializedName("name")
    private String name;

    @SerializedName("flag")
    private String flag;

    @SerializedName("capital")
    private String capital;

    @SerializedName("subregion")
    private String subregion;

    @SerializedName("alpha2Code")
    private String alpha2Code;

    @SerializedName("region")
    private String region;


    public String getPopulation() {
        double dPopulation = Double.parseDouble(population);
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(dPopulation);
    }
    public  String getName(){return  name;}
    public  String getFlag(){return  flag;}

    public  String getCapital(){return  capital;}
    public  String getSubregion(){return  subregion;}

    public String[] getValues(){
        return new String[] {getPopulation(), capital,subregion, name, alpha2Code,region};

    }
    public String[] getHeader(){
        return new String[] {"Population", "Capital", "Subregion", "Name", "Code", "Region"};
    }
}