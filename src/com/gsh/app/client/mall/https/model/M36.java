
package com.gsh.app.client.mall.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/3/12.
 * 小区单元楼层信息
 */
public class M36 implements Serializable {
    public long id;
    public String name;
    public long parentId;
    public List<M36> children;
}

