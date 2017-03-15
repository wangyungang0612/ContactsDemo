package com.chuck.contactsdemo;

/**
 * Created by Administrator on 2015/11/23.
 */
public class ContactsModel {
    private String name;
    private String phone;
    private String phonebook_label;

    public String getPhonebook_label() {
        return phonebook_label;
    }

    public void setPhonebook_label(String phonebook_label) {
        this.phonebook_label = phonebook_label;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
