package pt.tecnico.rec;

import java.util.*;

public class MutableUser{

    private HashMap<String, Integer> balance = new HashMap<String, Integer>();

    public MutableUser(String id){
        balance.put(id, 0);
    }
}