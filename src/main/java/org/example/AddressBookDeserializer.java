package org.example;

import tutorial.Addressbook;

import java.io.*;

public class AddressBookDeserializer {

    // 主函数：从文件中读取字节数组并执行反序列化操作。
    public static void main(String[] args) throws Exception {
        String addressBookFile = "addressbook.dat";

        // 从文件中读取字节数组。
        FileInputStream input = new FileInputStream(addressBookFile);
        byte[] serializedData = new byte[input.available()];
        input.read(serializedData);
        input.close();

        // 反序列化AddressBook
        Addressbook.AddressBook addressBook = Addressbook.AddressBook.parseFrom(serializedData);

        // 打印地址簿信息。
        for (Addressbook.Person person : addressBook.getPeopleList()) {
            System.out.println("用户名: " + person.getUsername());
            System.out.println("手机号: " + person.getPhone());
            if (!person.getEmail().isEmpty()) {
                System.out.println("电子邮件: " + person.getEmail());
            }
            System.out.println("性别: " + person.getGender());
            System.out.println();
        }
    }
}
