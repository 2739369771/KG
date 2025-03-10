package com.warmer.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.warmer.base.common.PageRecord;
import com.warmer.base.util.R;
import com.warmer.meta.dto.DataTableSubmitItem;
import com.warmer.meta.dto.DatasourceSubmitItem;
import com.warmer.meta.entity.MetaDataSource;
import com.warmer.meta.entity.MetaDataTable;
import com.warmer.meta.query.TableQuery;
import com.warmer.meta.service.MetaDataColumnService;
import com.warmer.meta.service.MetaDataSourceService;
import com.warmer.meta.service.MetaDataTableService;
import com.warmer.meta.vo.DataColumnVo;
import com.warmer.meta.vo.DataTableVo;
import com.warmer.meta.vo.DatasourceVo;
import com.warmer.web.entity.KgDomain;
import com.warmer.web.entity.Rescode;
import com.warmer.web.request.GraphItem;
import com.warmer.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.warmer.web.entity.location;

@Controller
@RequestMapping("/datasource")
public class DatasourceController extends BaseController {

    @Autowired
    MetaDataSourceService metaDataSourceService;
    @Autowired
    MetaDataTableService metaDataTableService;

    @Autowired
    MetaDataColumnService metaDataColumnService;

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String Index() {
        return "/datasource/index";
    }

    @ResponseBody
    @GetMapping(value = "/getYjNum")
    public List<Long> getYJSize() {
        return userService.getYjSize();
    }

//    public R<Rescode> WaterLevel(@RequestBody Rescode rescode)throws Exception
//    {
//        System.out.println(rescode);
////        List<Float> list =userService.getWaterLevel(resCd);
////        System.out.println("contorller层："+list);
//        return R.success(rescode, "操作成功");
//    }
    @GetMapping("/waterlevel")
    @ResponseBody
    public R<List<Float>> WaterLevel(String res_cd){
        System.out.println(res_cd);
        List<Float> list =userService.getWaterLevel(res_cd);
        System.out.println("contorller层："+list);
        return R.success(list, "操作成功");
    }
    @GetMapping("/getLocation")
    @ResponseBody
    public R<List<List<String>>> getLocation(String ad_cd) {
        List<List<String>> l = userService.getLocation(ad_cd);
        return R.success(l, "操作成功");
    }

    @GetMapping("/getDataSource")
    @ResponseBody
    public R<List<DatasourceVo>> getDataSource() {
        List<DatasourceVo> voList = metaDataSourceService.queryList();
        return R.success(voList, "操作成功");
    }

    @GetMapping("/getDataTable")
    @ResponseBody
    public R<List<DataTableVo>> getDataTable(Integer datasourceId) {
        List<DataTableVo> voList = metaDataTableService.queryByDatasourceId(datasourceId);
        return R.success(voList, "操作成功");
    }

    @GetMapping("/getDataColumn")
    @ResponseBody
    public R<List<DataColumnVo>> getDataColumn(Integer dataTableId) {
        List<DataColumnVo> voList = metaDataColumnService.queryByTableId(dataTableId);
        return R.success(voList, "操作成功");
    }
    @GetMapping("/getDataTableInfo")
    @ResponseBody
    public R<Map<String,Object>> getDataTableInfo(Integer dataTableId) {
        Map<String,Object> data=new HashMap<>();
        MetaDataTable metaDataTable = metaDataTableService.queryById(dataTableId);
        DataTableVo tableItem=new DataTableVo();
        tableItem.setDataTableId(metaDataTable.getDataTableId());
        tableItem.setDatasourceId(metaDataTable.getDatasourceId());
        tableItem.setDataTableName(metaDataTable.getDataTableCode());
        tableItem.setDataTableAlia(metaDataTable.getDataTableName());
        data.put("table",tableItem);
        List<DataColumnVo> voList = metaDataColumnService.queryByTableId(dataTableId);
        data.put("column",voList);
        return R.success(data, "操作成功");
    }
    @PostMapping("/getTableRecords")
    @ResponseBody
    public R<PageRecord<Map<String, Object>>> getTableRecords(@RequestBody TableQuery queryItem) {
        PageRecord<Map<String, Object>> voList = metaDataTableService.getTableRecords(queryItem);
        return R.success(voList, "操作成功");
    }
    /**
     * 保存数据源
     *
     * @param submitItem
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/saveDataSource")
    public R<Map<String,Object>> saveDataSource(@RequestBody DatasourceSubmitItem submitItem) {
        Map<String,Object> result=new HashMap<>();
        if (submitItem.getDataSourceId() == 0) {
            // 插入指标来源
            MetaDataSource dataSource = metaDataSourceService.insert(submitItem.transfer());
            ArrayList<String> tableList = metaDataTableService.getMetaTables(submitItem.transfer());
            result.put("sourceId",dataSource.getDataSourceId());
            result.put("tables",tableList);
        }else{
            return R.error("暂不支持编辑");
        }
        return R.success(result, "操作成功");
    }
    /**
     * 保存数据源
     *
     * @param submitItem
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveDataTable")
    public R<Map<String,Object>> saveDataTable(@RequestBody DataTableSubmitItem submitItem) {
        Map<String,Object> result=new HashMap<>();
        MetaDataSource dataSource = metaDataSourceService.queryById(submitItem.getDataSourceId());
        if(dataSource==null){
            return R.error("数据源不存在");
        }
        metaDataTableService.saveTables(submitItem);
        return R.success(result, "操作成功");
    }
}
