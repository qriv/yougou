package com.gsh.app.client.mall.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/2/27.
 */
//Community 社区信息
public class M2 implements Serializable {
    public long id;
    public String name;
    public String address;
    public String station;
    public long stationId;
    public List<M36> buildings;


}
