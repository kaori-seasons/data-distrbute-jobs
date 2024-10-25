package com.sucheon.jobs.mock.notify;

import com.sucheon.jobs.event.EventBean;

import java.util.List;

public interface ProcessSubscriber{


    List<EventRecevier> registerEventReceviver(EventRecevier eventReceviverByInterrupt);


    List<EventRecevier> removeEventReceviver(EventRecevier eventRecevier);

}
