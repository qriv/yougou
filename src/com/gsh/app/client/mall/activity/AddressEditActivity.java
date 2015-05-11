package com.gsh.app.client.mall.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M18;
import com.gsh.app.client.mall.https.model.M2;
import com.gsh.app.client.mall.https.model.M36;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.common.utils.CMUtil;
import com.litesuits.common.utils.StringUtils;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taosj on 15/2/3.
 */
public class AddressEditActivity extends ActivityBase {

    @InjectView
    private TextView community, building, unit, floor;
    @InjectView
    private EditText name, tel, address;
    private M18 m18;

    public static final ArrayList<M2> communities = CMUtil.getArrayList();
    private long currentCommunityId;
    private String addressInfo;
    private long currentBuildingId;
    private long currentUnitId;
    private long currentFloorId;

    private boolean isModify;
    private boolean isFirstChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_address_edit);
        m18 = (M18) getIntent().getSerializableExtra(M18.class.getName());

        setTitleBar(false, getString(R.string.title_add_address), RightAction.TEXT, "提交");
        Injector.self.inject(this);
        findViewById(R.id.pop).setVisibility(View.GONE);
        if (m18 != null) {
            communityName = m18.communityName;
            setTitleBar(false, "编辑地址", RightAction.TEXT, "提交");
            name.setText(m18.name);
            tel.setText(m18.mobile);
            address.setText(m18.address);
            community.setText(m18.communityName);
            building.setText(String.format("%s栋", m18.buildingName));
            unit.setText(String.format("%s单元", m18.floorName));

            currentCommunityId = m18.communityId;
            currentBuildingId = m18.buildingId;
            currentUnitId = m18.unitId;
            currentFloorId = m18.floorId;
        }


        reloadCommunities();

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstChange = true;
                go.name(AddressDialogActivity.class).param("data", communities).goForResult(0x9527);
            }
        });

        floor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (M2 m2 : communities) {
                    if (currentCommunityId == m2.id) {
                        final List<M36> buildings = m2.buildings.get(0).children;
                        if (buildings != null && buildings.size() > 0) {
                            for (M36 buildItem : buildings) {
                                if (buildItem.id == currentBuildingId) {

                                    final List<M36> currentUnits = buildItem.children;

                                    if (currentUnits.size() > 0) {

                                        for (M36 unitItem : currentUnits) {
                                            if (unitItem.id == currentUnitId) {

                                                final List<M36> currentFloors = unitItem.children;

                                                if (currentFloors.size() > 0) {
                                                    String[] items = new String[currentFloors.size()];
                                                    for (int i = 0; i < items.length; i++) {
                                                        items[i] = currentFloors.get(i).name;
                                                    }
                                                    new AlertDialog.Builder(context).setItems(items, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if (which > -1) {
                                                                isFirstChange = true;
                                                                currentFloorId = currentFloors.get(which).id;
                                                                floor.setText(currentFloors.get(which).name + "-楼");
                                                            }
                                                        }
                                                    }).create().show();
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        });

        building.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (M2 m2 : communities) {
                    if (currentCommunityId == m2.id) {
                        final List<M36> buildings = m2.buildings.get(0).children;
                        if (buildings.size() > 0) {
                            String[] items = new String[buildings.size()];
                            for (int i = 0; i < items.length; i++) {
                                items[i] = buildings.get(i).name;
                            }
                            new AlertDialog.Builder(context).setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which > -1) {
                                        isFirstChange = true;
                                        M36 m36 = buildings.get(which);
                                        currentBuildingId = m36.id;
                                        building.setText(m36.name + "-栋");
                                        unit.setText("-单元");
                                        floor.setText("-楼");
                                        currentUnitId = 0;
                                        currentFloorId = 0;
                                    }
                                }
                            }).create().show();
                        }
                        break;
                    }

                }
            }
        });

        unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (M2 m2 : communities) {
                    if (currentCommunityId == m2.id) {
                        final List<M36> buildings = m2.buildings.get(0).children;
                        if (buildings != null && buildings.size() > 0) {
                            for (M36 buildItem : buildings) {
                                if (buildItem.id == currentBuildingId) {

                                    final List<M36> currentUnits = buildItem.children;

                                    if (currentUnits.size() > 0) {
                                        String[] items = new String[currentUnits.size()];
                                        for (int i = 0; i < items.length; i++) {
                                            items[i] = currentUnits.get(i).name;
                                        }
                                        new AlertDialog.Builder(context).setItems(items, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (which > -1) {
                                                    isFirstChange = true;
                                                    M36 m36 = currentUnits.get(which);
                                                    floor.setText("-楼");
                                                    currentUnitId = m36.id;
                                                    unit.setText(m36.name + "-单元");
                                                    currentFloorId = 0;
                                                }
                                            }
                                        }).create().show();
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        });
    }

    private void reloadCommunities() {
        showProgressDialog();
        hideContent();
        execute(new Request(Urls.COMMUNITY_LIST), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                dismissProgressDialog();
                if (apiResult.isOk()) {
                    showContent();
                    communities.clear();
                    List<M2> data = apiResult.getModels(M2.class);
                    for (M2 item : data) {
                        communities.add(item);
                        if (m18 == null) {
                            if (MallApplication.user.getCommunityId() == item.id) {
                                currentCommunityId = item.id;
                                community.setText(item.name);
                            }
                        }
                    }
                    if (isFirstChange) {
                        building.setText("-");
                        currentBuildingId = 0;
                        unit.setText("-");
                        currentUnitId = 0;
                        floor.setText("-");
                        currentFloorId = 0;
                    }
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                dismissProgressDialog();
            }
        });
    }

    private String communityName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x9527 && resultCode == RESULT_OK) {
            M2 item = (M2) data.getSerializableExtra("selectedItem");
            currentCommunityId = item.id;
            communityName = item.name;
            community.setText(item.name);
//            reloadCommunities(item.stationId);
        }
    }

    @Override
    protected void onRightActionPressed() {
        if (currentCommunityId == 0) {
            toast("必须选择社区");
            return;
        }
        /*if (currentBuildingId == 0) {
            toast("必须选择栋");
            return;
        }
        if (currentUnitId == 0) {
            toast("必须选择单元");
            return;
        }*/
        final String houseNumber = address.getText().toString().trim();
        if (TextUtils.isEmpty(houseNumber)) {
            toast("必须填写门牌号");
            return;
        }
        final String contact = name.getText().toString().trim();
        if (TextUtils.isEmpty(contact)) {
            toast("必须填写联系人");
            return;
        }
        final String mobile = tel.getText().toString().trim();
        if (!StringUtils.checkTelephone(mobile)) {
            toast("必须填写联系电话");
            return;
        }


        showProgressDialog();
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(community.getText().toString());
     /*   stringBuilder.append(building.getText().toString());
        stringBuilder.append(unit.getText().toString());
     */
        stringBuilder.append(address.getText().toString());
        final String addressDetail = stringBuilder.toString();
        if (currentFloorId != 0)
            stringBuilder.append(floor.getText().toString());
        if (m18 == null) {
            execute(new Request(Urls.MEMBER_ADDRESS_CREATE).addUrlParam("name", contact)
                    .addUrlParam("address", address.getText().toString().trim())
                    .addUrlParam("mobile", mobile)
                    .addUrlParam("communityId", currentCommunityId + "")
       /*             .addUrlParam("buildingId", currentBuildingId + "")
                    .addUrlParam("unitId", currentUnitId + "")
                    .addUrlParam("floorId", currentFloorId + "")
       */
                    .addUrlParam("name", name.getText().toString())
                    .addUrlParam("addressDetail", addressDetail), new HttpResultHandler() {
                @Override
                protected void onSuccess(ApiResult apiResult) {
                    dismissProgressDialog();
                    if (apiResult.isOk()) {
                        isModify = true;
                        if (!TextUtils.isEmpty(getIntent().getStringExtra(String.class.getName()))) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            onBackPressed();
                        }
                    }
                }

                @Override
                protected void onFailure(Response res, HttpException e) {
                    dismissProgressDialog();
                }
            });
        } else {
            execute(new Request(Urls.MEMBER_ADDRESS_CHANGE).addUrlParam("name", name.getText().toString().trim())
                    .addUrlParam("address", address.getText().toString().trim())
                    .addUrlParam("mobile", tel.getText().toString().trim())
                    .addUrlParam("communityId", currentCommunityId + "")
                   /* .addUrlParam("buildingId", currentBuildingId + "")
                    .addUrlParam("unitId", currentUnitId + "")
                    .addUrlParam("floorId", currentFloorId + "")*/
                    .addUrlParam("id", m18.id + "")
                    .addUrlParam("name", name.getText().toString())
                    .addUrlParam("addressDetail", stringBuilder.toString()), new HttpResultHandler() {
                @Override
                protected void onSuccess(ApiResult apiResult) {
                    dismissProgressDialog();
                    if (apiResult.isOk()) {
                        isModify = true;
                        Intent intent = new Intent();

                        M18 m = new M18();
                        m.id = m18.id;
                        m.communityId = currentCommunityId;
                        m.communityName = communityName;
                        m.address = address.getText().toString().trim();
                        m.mobile = tel.getText().toString().trim();
                        m.name = name.getText().toString();
                        intent.putExtra(M18.class.getName(), m);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

                @Override
                protected void onFailure(Response res, HttpException e) {
                    dismissProgressDialog();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (isModify) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }

        super.onBackPressed();
    }
}
