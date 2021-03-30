package pt.tecnico.bicloin.hub;

public class UserAccount {
    private String _id;
    private String _name;
    private String _phone;
    private Integer _balance;

    public UserAccount(String id, String name, String phone) {
        _id = id;
        _name = name;
        _phone = phone;
        _balance = 0;
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

    public Integer getBalance(){
        return _balance;
    }

    public void setBalance(int balance){
        _balance += 10 * balance;
    }
}