/*
 * Copyright (c) 2014 Gangshanghua Information Technologies Ltd.
 * http://www.gangsh.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Gangshanghua Information Technologies ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you
 * entered into with Gangshanghua Information Technologies.
 */

package com.gsh.app.client.mall;

/**
 * 常量
 *
 * @author Tan Chunmao
 */
public final class Constant {
    public static final class Notification {
    }

    public static final class Pref {
        public static final String HOST_NAME = "com.gsh.app.client.mall.Pref.HOST_NAME";//服务站名称
        public static final String COMMUNITY_ID = "com.gsh.app.client.mall.Pref.COMMUNITY_ID";//社区Id
        public static final String TOKEN = "com.gsh.app.client.mall.Pref.TOKEN";//用户token
        public static final String LOG_IN = "com.gsh.app.client.mall.Pref.LOG_IN";//是否登录
        public static final String LOGIN_USER = "com.gsh.app.client.mall.Pref.LOGIN_USER";//登录用户信息
    }

    public static final class CountLimit {
        public static final int SHARE_PICTURE = 4;//单条分享所能上传图片数量
        public static final int PAGE_SIZE = 20;//分页接口每页的数量
        public static final int AVATAR_SIZE = 300;//头像尺寸
        public static final int UPLOAD_PICTURE_SIZE = 720;//上传图片压缩
    }

    public static final class TestData {
        public static final int[] pics = {R.drawable.fruit_a, R.drawable.fruit_b, R.drawable.fruit_c, R.drawable.fruit_d, R.drawable.fruit_e, R.drawable.fruit_f, R.drawable.fruit_g, R.drawable.fruit_h};
        public static final String avatar_male = "http://img0.bdstatic.com/img/image/6df6fc5e19d538785f9fb87e2e76b98f1409125627.jpg";
        public static final String avatar_female = "http://img0.bdstatic.com/img/image/e538cf88a26fc6f936b3047a3e12ceb81409111482.jpg";
        public static final String[] fruit_pictures = {"http://img0.imgtn.bdimg.com/it/u=2251909169,311295289&fm=21&gp=0.jpg", "http://img1.imgtn.bdimg.com/it/u=3583647138,2653046993&fm=21&gp=0.jpg", "http://img5.imgtn.bdimg.com/it/u=3322368831,3685505520&fm=21&gp=0.jpg"};
        public static final String[] fruit_names = {"东南亚特级火龙五", "福建空运新鲜龙眼", "台湾大个顶级台农芒果", "泰国牛油果", "湖北秭归顶级脐橙", "智利新鲜大个好吃车厘子", "四川苍溪特产红心猕猴桃", "日本进口红富士苹果"};
        public static final String[] people_names = {"张三", "李四", "王麻子"};
        public static final String[] package_picture = {"http://img5.imgtn.bdimg.com/it/u=1049855540,3542291185&fm=21&gp=0.jpg", "http://img0.imgtn.bdimg.com/it/u=1244764762,2318830034&fm=21&gp=0.jpg", "http://img3.imgtn.bdimg.com/it/u=742484217,2875511976&fm=21&gp=0.jpg"};
        public static final String[] square_pictures = {"http://img0.bdstatic.com/img/image/shouye/sheying0415.jpg", "http://img0.bdstatic.com/img/image/shouye/bizhi0415.jpg", "http://img0.bdstatic.com/img/image/shouye/meishi0415.jpg"};
        public static final String avatar_test = "20141215/20141215132121_ZaN0CIm4jMg0pmjSprxyI7Bo60nvnZmw_284059.png";
    }

    public static final class Cache {
        public static final String ADDRESS_LIST = "address_list.txt";
        public static final String BASKET_LIST = "basket_list.txt";
        public static final String COLLECTION_LIST = "collection_list.txt";
        public static final String VOUCHER_LIST = "voucher_list.txt";
        public static final String PAYMENT_LIST = "payment_list.txt";
        public static final String WALLET = "wallet.txt";
        public static final String POINT_RECORD_LIST = "point_record_list.txt";
        public static final String ORDER_LIST = "order_list.txt";
        public static final String COMMENT_LIST = "comment_list.txt";
        public static final String[] PICTURE_SAMPLES = new String[]{"pic_a.png", "pic_b.png", "pic_c.png", "pic_d.png", "pic_e.png", "pic_f.png", "pic_g.png", "pic_h.png"};
    }


}
