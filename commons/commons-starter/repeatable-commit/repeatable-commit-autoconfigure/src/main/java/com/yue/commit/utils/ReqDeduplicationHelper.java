package com.yue.commit.utils;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

//请求去重
public class ReqDeduplicationHelper {

    public String deduplicationParamMD5(final String reqJSON, String... excludeKeys) {
        TreeMap paramTreeMap = JSON.parseObject(reqJSON, TreeMap.class);
        if (excludeKeys!=null) {
            List<String> dedupExcludeKeys = Arrays.asList(excludeKeys);
            if (!dedupExcludeKeys.isEmpty()) {
                for (String dedupExcludeKey : dedupExcludeKeys) {
                    paramTreeMap.remove(dedupExcludeKey);
                }
            }
        }
        String paramTreeMapJSON = JSON.toJSONString(paramTreeMap);
        return jdkMD5(paramTreeMapJSON);
    }

    private static String jdkMD5(String src) {
        String res = null;
        try {
            res = DigestUtil.md5Hex(src);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return res;
    }
}
