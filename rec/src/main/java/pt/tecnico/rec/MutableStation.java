package pt.tecnico.rec;


public class MutableStation{

    private Integer _deliveries;
    private Integer _requisitions;
    private Integer _docks;
    private Integer _bikes;
    private Integer _tagStation;


    public MutableStation(String abbr){
        _deliveries = 0;
        _requisitions = 0;
        _docks = 0;
        _bikes = 0;
        _tagStation = 0;
    }

    public MutableStation(String abbr, Integer docksNr, Integer bikesNr){
        _requisitions = docksNr - bikesNr;
        _deliveries = 0;
        _docks = docksNr;
        _bikes = bikesNr;
        _tagStation = 0;
    }

    public Integer getTagStation(){
        return _tagStation;
    }

    public void setTagStation(Integer newTag){
        _tagStation = newTag;
    }

    public Integer getAvailableBikesNr(){
        return _bikes;
    }

    public Integer getDocksNumber() {
        return _docks;
    }

    public void bikeDown(){
        _bikes++;
        _deliveries++;
        _tagStation++;
    }

    public void bikeUp(){
        _requisitions++;
        _bikes--;
        _tagStation++;
    }

    public Integer getDeliveries() {
        return _deliveries;
    }

    public Integer getRequisitions() {
        return _requisitions;
    }

    public void updateStats(Integer req, Integer del, Integer docks, Integer bikes){
        _requisitions = req;
        _deliveries = del;
        _docks = docks;
        _bikes = bikes;
    }
}