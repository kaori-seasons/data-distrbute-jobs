package com.sucheon.jobs.functions;

import com.sucheon.jobs.event.RuleMatchResult;
import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

/**
 * 利用master+worker进行多kafka分发
 */
public class DeliveryMutliTopicSinkFunction implements SinkFunction<RuleMatchResult> {

    @Override
    public void invoke(RuleMatchResult value, Context context) throws Exception {



        SinkFunction.super.invoke(value, context);
    }

    @Override
    public void writeWatermark(Watermark watermark) throws Exception {
        SinkFunction.super.writeWatermark(watermark);
    }

    @Override
    public void finish() throws Exception {
        SinkFunction.super.finish();
    }
}
