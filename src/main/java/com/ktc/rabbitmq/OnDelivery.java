package com.ktc.rabbitmq;

import com.rabbitmq.client.Delivery;

interface OnDelivery{
    void onDelivery(Delivery delivery);

}