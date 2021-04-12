package pt.tecnico.rec;

import java.util.*;
import org.apache.commons.lang3.tuple.MutablePair;

public class MutableUser{

    private static MutablePair<String, Integer> _balance = new MutablePair<String, Integer>();

    public MutableUser(String id){
        _balance.setLeft(id + "/balance");
        _balance.setRight(0);
    }

    public Integer getBalance(){
        return _balance.getRight();
    }

    public void increaseBalance(Integer balance){
        Integer previousBalance = _balance.getValue();
        _balance.setValue(previousBalance + (balance*10));
    }
}