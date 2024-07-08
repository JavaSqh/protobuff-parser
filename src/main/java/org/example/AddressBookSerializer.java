package org.example;


import tutorial.Addressbook;

import java.io.*;

/**
 * protobuf序列化
 *
 * @author 公众号： Java编程与思想
 */
public class AddressBookSerializer {

    public static void main(String[] args) throws Exception {
        // 主函数：硬编码数据并执行序列化操作。
        Addressbook.AddressBook.Builder addressBook = Addressbook.AddressBook.newBuilder();
        // 添加一个人员信息（硬编码数据）。
        Addressbook.Person person1 = Addressbook.Person.newBuilder()
                .setUsername("Alice")
                .setPhone("1234567890")
                .setEmail("alice@example.com")
                .setGender(Addressbook.Person.Gender.FEMALE)
                .build();

        addressBook.addPeople(person1);

        // 将地址簿写入字节数组。
        byte[] serializedData = addressBook.build().toByteArray();

        // 将字节数组写入文件。
        String addressBookFile = "addressbook.dat";
        FileOutputStream output = new FileOutputStream(addressBookFile);
        output.write(serializedData);
        output.close();

        System.out.println("序列化成功，数据已写入 " + addressBookFile);
    }
}
