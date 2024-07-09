package org.example;

import com.google.protobuf.*;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

/**
 * 通过通用proto, 进行动态解析prootobuf流
 *
 * @author 公众号： Java编程与思想
 */
public class AddressBookDynamicDeserializerpPro {
    public static void main(String[] args) throws Exception {
        // 生成并序列化 AddressBook 消息
        Addressbook.AddressBook addressBook = initMsg();

        // 从文件 "./src/main/proto/cinema1.description" 中解析 FileDescriptorSet 对象
        DescriptorProtos.FileDescriptorSet descriptorSet = DescriptorProtos.FileDescriptorSet
                .parseFrom(new FileInputStream("./src/main/proto/cinema1.description"));

        // 创建 SelfDescribingMessage 的构建器
        Selfmd.SelfDescribingMessage.Builder selfmdBuilder = Selfmd.SelfDescribingMessage.newBuilder();
        // 设置描述符集合
        selfmdBuilder.setDescriptorSet(descriptorSet);
        // 设置消息名称
        selfmdBuilder.setMsgName(Addressbook.AddressBook.getDescriptor().getFullName());
        // 设置消息内容（序列化的字节数组）
        selfmdBuilder.setMessage(addressBook.toByteString());
        // 构建 SelfDescribingMessage 并序列化为字节数组
        byte[] byteArray = selfmdBuilder.build().toByteArray();

        // 从字节数组中反序列化 SelfDescribingMessage 对象
        Selfmd.SelfDescribingMessage parseFrom = Selfmd.SelfDescribingMessage.parseFrom(byteArray);
        // 获取描述符集合
        DescriptorProtos.FileDescriptorSet descriptorSet2 = parseFrom.getDescriptorSet();
        // 获取消息内容（字节串）
        ByteString message = parseFrom.getMessage();
        // 获取消息名称
        String msgName = parseFrom.getMsgName();

        Descriptors.Descriptor pbDescritpor = null;
        // 遍历每一个 FileDescriptorProto 对象
        for (DescriptorProtos.FileDescriptorProto fdp : descriptorSet2.getFileList()) {
            // 构建 FileDescriptor 对象
            Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor
                    .buildFrom(fdp, new Descriptors.FileDescriptor[] {});
            // 遍历每一个消息类型（Descriptor 对象）
            for (Descriptors.Descriptor descriptor : fileDescriptor.getMessageTypes()) {
                // 查找名称与 msgName 匹配的消息类型
                if (descriptor.getName().equals(msgName)) {
                    System.out.println("descriptor found");
                    pbDescritpor = descriptor;
                    break;
                }
            }
        }

        // 如果没有找到匹配的描述符，打印错误信息并返回
        if (pbDescritpor == null) {
            System.out.println("No matched descriptor");
            return;
        }

        // 使用找到的描述符解析消息内容
        DynamicMessage pbMessage = DynamicMessage.parseFrom(pbDescritpor, message);
        // 打印消息内容
        printMessage(pbMessage);
    }

    private static void printMessage(Message message) {
        // 遍历消息中的所有字段
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
            Descriptors.FieldDescriptor field = entry.getKey();
            Object value = entry.getValue();

            // 处理重复字段
            if (field.isRepeated()) {
                List<?> values = (List<?>) value;
                for (Object item : values) {
                    // 如果值是消息类型，递归调用 printMessage 方法
                    if (item instanceof Message) {
                        printMessage((Message) item);
                    }
                }
            } else {
                // 如果值是消息类型，递归调用 printMessage 方法
                if (value instanceof Message) {
                    printMessage((Message) value);
                } else {
                    // 打印字段的 JSON 名称和字段值
                    System.out.println(field.getJsonName() + ": "  + value.toString());
                }
            }
        }
    }

    private static Addressbook.AddressBook initMsg() {
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

        // 构建并返回 AddressBook 对象
        return addressBook.build();
    }
}

