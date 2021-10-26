package com.ktc.rabbitmq;

import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Channel;

interface OnDelivery{
    void onDelivery(Delivery delivery,String processName);

}