package org.jeecg.modules.coderQ.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.coderQ.entity.SqDbinfo;
import org.jeecg.modules.coderQ.service.ISqDbinfoService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

/**
 * @Description: sq_dbinfo
 * @Author:
 * @Date: 2023-06-06
 * @Version: V1.0
 */
@Api(tags = "sq_dbinfo")
@RestController
@RequestMapping("/coderQ/sqDbinfo")
@Slf4j
public class SqDbinfoController extends JeecgController<SqDbinfo, ISqDbinfoService> {
    @Autowired
    private ISqDbinfoService sqDbinfoService;

    /**
     * 分页列表查询
     *
     * @param sqDbinfo
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "sq_dbinfo-分页列表查询")
    @ApiOperation(value = "sq_dbinfo-分页列表查询", notes = "sq_dbinfo-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(SqDbinfo sqDbinfo,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        LambdaQueryWrapper<SqDbinfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(sqDbinfo.getName()), SqDbinfo::getName, sqDbinfo.getName());
        queryWrapper.like(StrUtil.isNotBlank(sqDbinfo.getVersion()), SqDbinfo::getVersion, sqDbinfo.getVersion());
        queryWrapper.orderByDesc(SqDbinfo::getCreateTime);

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (!loginUser.getUsername().equals("admin")) {
            queryWrapper.eq(SqDbinfo::getCreateBy, loginUser.getUsername());
        }

        Page<SqDbinfo> page = new Page<SqDbinfo>(pageNo, pageSize);
        IPage<SqDbinfo> pageList = sqDbinfoService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param sqDbinfo
     * @return
     */
    @AutoLog(value = "sq_dbinfo-添加")
    @ApiOperation(value = "sq_dbinfo-添加", notes = "sq_dbinfo-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SqDbinfo sqDbinfo) {
        sqDbinfoService.save(sqDbinfo);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param sqDbinfo
     * @return
     */
    @AutoLog(value = "sq_dbinfo-编辑")
    @ApiOperation(value = "sq_dbinfo-编辑", notes = "sq_dbinfo-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody SqDbinfo sqDbinfo) {
        sqDbinfoService.updateById(sqDbinfo);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "sq_dbinfo-通过id删除")
    @ApiOperation(value = "sq_dbinfo-通过id删除", notes = "sq_dbinfo-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sqDbinfoService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "sq_dbinfo-批量删除")
    @ApiOperation(value = "sq_dbinfo-批量删除", notes = "sq_dbinfo-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sqDbinfoService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "sq_dbinfo-通过id查询")
    @ApiOperation(value = "sq_dbinfo-通过id查询", notes = "sq_dbinfo-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        SqDbinfo sqDbinfo = sqDbinfoService.getById(id);
        if (sqDbinfo == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(sqDbinfo);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param sqDbinfo
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SqDbinfo sqDbinfo) {
        return super.exportXls(request, sqDbinfo, SqDbinfo.class, "sq_dbinfo");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SqDbinfo.class);
    }

}
