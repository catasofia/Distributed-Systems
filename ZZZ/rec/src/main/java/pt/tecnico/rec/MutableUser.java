package pt.tecnico.rec;

import org.apache.commons.lang3.tuple.MutablePair;

public class MutableUser{

    private MutablePair<String, Integer> _balance = new MutablePair<String, Integer>();
    private boolean _bike;
    private Integer _tagBalance;
    private Integer _tagBike;

    public MutableUser(String id){
        _balance.setLeft(id + "/balance");
        _balance.setRight(0);
        _bike = false;
        _tagBalance = 0;
        _tagBike = 0;
    }

    public String getId(){
        return _balance.getLeft();
    }

    public Integer getBalance(){
        return _balance.getRight();
    }

    public Integer getTagBalance(){
        return _tagBalance;
    }

    public Integer getTagBike(){
        return _tagBike;
    }

    public void setNewBalance(Integer newBalance){
        _balance.setValue(newBalance);
    }

    public void setTagBalance(Integer newTag){
        _tagBalance = newTag;
    }

    public void increaseBalance(Integer balance){
        Integer previousBalance = _balance.getValue();
        _balance.setValue(previousBalance + balance);
        _tagBalance++;
    }

    public boolean getBikeState() {
        return _bike;
    }

    public String getStringState() {
        if(_bike) return "true";
        else return "false";
    }

    public void setNewState(boolean state){
        _bike = state;
    }

    public void setBikeState() {
        _bike = !_bike;
        _tagBike++;
    }
}