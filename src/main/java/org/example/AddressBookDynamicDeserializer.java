package org.example;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

/**
 * 通过生成descriptor 文件, 进行动态解析prootobuf流
 *
 * @author 公众号： Java编程与思想
 */
public class AddressBookDynamicDeserializer {
    public static void main(String[] args) throws Exception {

        // 生成 descriptor 文件的 protoc 命令
        String protocCMD = "protoc --descriptor_set_out=./src/main/proto/cinema1.description ./src/main/proto/addressbook.proto --proto_path=./";
        // 执行 protoc 命令生成 descriptor 文件
        Process process = Runtime.getRuntime().exec(protocCMD);
        // 等待命令执行完成
        process.waitFor();
        int exitValue = process.exitValue();
        if (exitValue != 0) {
            // 如果命令执行失败，打印错误信息并返回
            System.out.println("protoc execute failed");
            return;
        }

        Descriptors.Descriptor pbDescritpor = null;

        // 从文件 "./src/main/proto/cinema1.description" 中解析 FileDescriptorSet 对象
        DescriptorProtos.FileDescriptorSet descriptorSet = DescriptorProtos.FileDescriptorSet
                .parseFrom(new FileInputStream("./src/main/proto/cinema1.description"));

        // 遍历每一个 FileDescriptorProto 对象
        for (DescriptorProtos.FileDescriptorProto fdp : descriptorSet.getFileList()) {
            // 构建 FileDescriptor 对象
            Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor.buildFrom(fdp, new Descriptors.FileDescriptor[] {});
            // 遍历每一个消息类型（Descriptor 对象）
            for (Descriptors.Descriptor descriptor : fileDescriptor.getMessageTypes()) {
                // 查找名称为 "AddressBook" 的消息类型
                if (descriptor.getName().equals("AddressBook")) {
                    pbDescritpor = descriptor;
                    break;
                }
            }
        }

        // protobuf序列化后的byte[]
        byte[] bytes = initMsg();

        // 创建一个新的 DynamicMessage.Builder，用于构建动态消息
        DynamicMessage.Builder pbBuilder = DynamicMessage.newBuilder(pbDescritpor);
        // 从序列化数据中合并消息
        Message pbMessage = pbBuilder.mergeFrom(bytes).build();
        // 打印消息内容
        printMessage(pbMessage);
    }

    private static void printMessage(Message message) {
        // 遍历消息中的所有字段
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
            Descriptors.FieldDescriptor field = entry.getKey();
            Object value = entry.getValue();

            if (field.isRepeated()) {
                // 处理重复字段
                List<?> values = (List<?>) value;
                for (Object item : values) {
                    if (item instanceof Message) {
                        // 如果值是消息类型，递归调用 printMessage 方法
                        printMessage((Message) item);
                    }
                }
            } else {
                if (value instanceof Message) {
                    // 如果值是消息类型，递归调用 printMessage 方法
                    printMessage((Message) value);
                } else {
                    // 打印字段的 JSON 名称和字段值
                    System.out.println(field.getJsonName() + ": "  + value.toString());
                }
            }
        }
    }


    private static byte[] initMsg() {
        // 创建 AddressBook 的构建器
        Addressbook.AddressBook.Builder addressBook = Addressbook.AddressBook.newBuilder();

        // 添加一个人员信息（硬编码数据）
        Addressbook.Person person1 = Addressbook.Person.newBuilder()
                .setUsername("Alice")
                .setPhone("1234567890")
                .setEmail("alice@example.com")
                .setGender(Addressbook.Person.Gender.FEMALE)
                .build();

        // 将人员信息添加到地址簿
        addressBook.addPeople(person1);

        // 将地址簿构建成字节数组（序列化）
        byte[] serializedData = addressBook.build().toByteArray();

        return serializedData;
    }

}
