package com.gsh.app.client.mall.https.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by taosj on 15/2/27.
 */
//FavoriteGoods 收藏商品
public class M9 implements Serializable {
    public long id;
    public String name;
    public String picturePath;
    public List<String> picturePaths;
    public double price;
    public int stock;
    public long goodsId;
    public boolean isUndercarriage;
    public double orginPrice;

    public boolean isAvailable() {
        return stock > 0 && !isUndercarriage;
    }
}
