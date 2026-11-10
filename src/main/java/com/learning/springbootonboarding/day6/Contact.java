package com.learning.springbootonboarding.day6;

public class Contact {

    private final int id;
    private final String name;
    private final String email;
    private final String phone;

    public Contact(int id, String name, String email, String phone){

        if(id<0){
            System.out.println("Id required");
        }

        if(name==null || name.isBlank() || email==null || email.isBlank()){
            System.out.println("name or email are required");
        }

        if(Integer.parseInt(phone)<=0 ){
            System.out.println("Phone Number should be between 0 to 10 ");
        }

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public int getId(){
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName(){
        return name;
    }

    public  String getPhone(){
        return phone;
    }


    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}