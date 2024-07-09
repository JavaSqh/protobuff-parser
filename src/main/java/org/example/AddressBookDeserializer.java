package org.example;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * protobuf反序列化
 *
 * @author 公众号： Java编程与思想
 */
public class AddressBookDeserializer {
    public static void main(String[] args) throws Exception {
        // 主函数：从文件中读取字节数组并执行反序列化操作。
        String addressBookFile = "addressbook.dat";
        // 从文件中读取字节数组并反序列化AddressBook
        byte[] serializedData = Files.readAllBytes(Paths.get(addressBookFile));
        Addressbook.AddressBook addressBook = Addressbook.AddressBook.parseFrom(serializedData);
        // 处理addressBook对象
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
