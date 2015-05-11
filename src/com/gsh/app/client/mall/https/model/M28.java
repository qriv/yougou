package com.gsh.app.client.mall.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/2/27.
 */
//Station 服务站信息
public class M28 implements Serializable {
    public long id;
    public String name;
    public String address;
    public List<String> range;
}
