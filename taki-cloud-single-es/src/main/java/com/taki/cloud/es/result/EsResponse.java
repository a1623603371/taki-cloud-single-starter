package com.taki.cloud.es.result;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

/**
 * @ClassName EsResponse
 * @Description TODO
 * @Author Long
 * @Date 2023/6/12 16:49
 * @Version 1.0
 */
@Data
public class EsResponse {

    private long total;

    private JSONArray data;
}
