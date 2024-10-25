package com.sucheon.jobs.event;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBuf;
import org.apache.flink.shaded.netty4.io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * 命中对应算法规则之后的配置结果
 */
@Getter
@Setter
public class RuleMatchResult {

    /**
     * 结果命中之后传输到哪个topic
     */
    private String topic;

    /**
     * 该算法模版的code编码列表
     */
    private String keyValue;

    /**
     * 该算法模版归属的算法组
     */
    private String algGroup;

    /**
     * 测点还是算法实例类型
     */
    private EventBean eventBean;


    public byte[] toByteArray() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(topic.getBytes(StandardCharsets.UTF_8));
        buf.writeBytes(keyValue.getBytes(StandardCharsets.UTF_8));
        buf.writeBytes(algGroup.getBytes(StandardCharsets.UTF_8));
        byte[] eventBytes = eventBean.toString().getBytes(StandardCharsets.UTF_8);
        buf.writeBytes(eventBytes);
        return buf.array();
    }
}
