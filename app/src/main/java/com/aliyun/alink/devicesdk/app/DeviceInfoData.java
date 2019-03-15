package com.aliyun.alink.devicesdk.app;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.dm.api.BaseInfo;
import com.aliyun.alink.dm.api.DeviceInfo;

import java.util.List;

/*
 * Copyright (c) 2014-2016 Alibaba Group. All rights reserved.
 * License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class DeviceInfoData {
    /**
     * 网关设备 测试套件高阶版
     * 在测试输入该值的时候，需要同时填写子设备的信息
     * 由于安全策略原因，子设备的pk、dn都必须要在云端已经添加才可以
     */
    public DeviceInfo gateway = null;
    /**
     * 与网关关联的子设备信息
     * 后续网关测试demo 会 添加子设备 删除子设备 建立 topo关系 子设备上下线等
     */
    public List<BaseInfo> subDevice = null;
    /**
     * 套件基础版
     * 如果只想测试套件基础版的功能，只填写这部分的信息就够了
     */
    public DeviceInfo device = null;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
