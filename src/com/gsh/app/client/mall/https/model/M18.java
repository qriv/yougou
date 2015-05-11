package com.gsh.app.client.mall.https.model;

import java.io.Serializable;

/**
 * Created by taosj on 15/2/27.
 */
//Address 收货地址信息
public class M18 implements Serializable {

    public long id;
    public boolean isDefault;
    public String name;
    public String address;//门牌号
    public String mobile;
    public String communityName;
    public long communityId;
    public String buildingName;
    public long buildingId;
    public long unitId;
    public String unitName;
    public String floorName;
    public long floorId;
    public String addressDetail;//组装的详细地址

    public String deliveryDescription;


}
