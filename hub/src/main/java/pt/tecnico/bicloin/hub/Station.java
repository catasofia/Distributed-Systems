package pt.tecnico.bicloin.hub;
import java.util.ArrayList;
import java.util.List;

public class Station {
    private String _name;
    private String _abbr;
    private Double _latitude;
    private Double _longitude;
    private Integer _prize;
    private Integer _docksNr;
    //private Integer _bikesNr;

    public Station (String name, String abbr, Double latitude, Double longitude, Integer docksNr,
    /*Integer bikesNr,*/ Integer prize){
        _name = name;
        _abbr = abbr;
        _latitude = latitude;
        _longitude = longitude;
        _prize = prize;
        _docksNr = docksNr;
        //_bikesNr = bikesNr;
    }

    public String getName(){
        return _name;
    }

    public String getAbbr(){
        return _abbr;
    }
    
    public Double getLatitude(){
        return _latitude;
    }

    public Double getLongitude(){
        return _longitude;
    }

    public Integer getPrize(){
        return _prize;
    }

    public Integer getDocksNr(){
        return _docksNr;
    }

    /*public Integer getBikesNr(){
        return _bikesNr;
    }*/

    public Double calculateDistance(Double latitude, Double longitude){
        Integer earthRadius = 6371;
        
        Double dLat = Math.toRadians((latitude - _latitude));
        Double dLong = Math.toRadians((longitude - _longitude));

        Double startLat = Math.toRadians(latitude);
        Double endLat = Math.toRadians(_latitude);

        Double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRadius * c;
    }

    public Double haversin(Double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}