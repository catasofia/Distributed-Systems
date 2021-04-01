package pt.tecnico.bicloin.hub;

public class User {
    private String _id;
    private String _name;
    private String _phone;

    public User(String id, String name, String phone) {
        _id = id;
        _name = name;
        _phone = phone;
    }

    public String getId(){
        return _id;
    }

    public String getName(){
        return _name;
    }

    public String getPhone(){
        return _phone;
    }
}