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
        // protobuf序列化后的byte[]
        Addressbook.AddressBook addressBook = initMsg();

        DescriptorProtos.FileDescriptorSet descriptorSet = DescriptorProtos.FileDescriptorSet
                .parseFrom(new FileInputStream("./src/main/proto/cinema1.description"));

        Selfmd.SelfDescribingMessage.Builder selfmdBuilder = Selfmd.SelfDescribingMessage.newBuilder();
        selfmdBuilder.setDescriptorSet(descriptorSet);
        selfmdBuilder.setMsgName(Addressbook.AddressBook.getDescriptor().getFullName());
        selfmdBuilder.setMessage(addressBook.toByteString());
        byte[] byteArray = selfmdBuilder.build().toByteArray();

        Selfmd.SelfDescribingMessage parseFrom = Selfmd.SelfDescribingMessage.parseFrom(byteArray);
        DescriptorProtos.FileDescriptorSet descriptorSet2 = parseFrom.getDescriptorSet();
        ByteString message = parseFrom.getMessage();
        String msgName = parseFrom.getMsgName();

        Descriptors.Descriptor pbDescritpor = null;
        for (DescriptorProtos.FileDescriptorProto fdp : descriptorSet2
                .getFileList()) {
            Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor
                    .buildFrom(fdp, new Descriptors.FileDescriptor[] {});
            for (Descriptors.Descriptor descriptor : fileDescriptor
                    .getMessageTypes()) {
                if (descriptor.getName().equals(msgName)) {
                    System.out.println("descriptor found");
                    pbDescritpor = descriptor;
                    break;
                }
            }
        }

        if (pbDescritpor == null) {
            System.out.println("No matched descriptor");
            return;
        }
        DynamicMessage pbMessage = DynamicMessage.parseFrom(pbDescritpor, message);
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

        // 将地址簿构建成字节数组（序列化）
        Addressbook.AddressBook build = addressBook.build();

        return build;
    }

}
