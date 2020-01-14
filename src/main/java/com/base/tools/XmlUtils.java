/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import com.base.model.businessentity.toolsentity.ExportSettingEntity;
import com.base.model.businessentity.toolsentity.ExportSheetEntity;
import com.base.model.businessentity.toolsentity.ExportTitleEntity;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * XML文件操作类
 *
 * @author 司徒彬
 * @date 2016/10/17 22:53
 */
public class XmlUtils {
    private static final String ROOT_PATH = ReflectionUtils.getRootPath(XmlUtils.class);
    private static final String EXPORT_CONFIG = ROOT_PATH + "/systemconfig/exportconfig.xml";


    /**
     * Gets filter entities.
     *
     * @return the filter entities
     * @throws Exception the exception
     */
    public static String getKeyValue(String key, String xmlStr) throws Exception {
        try {
            SAXReader reader = new SAXReader();
            Element rootElement = reader.read(new ByteArrayInputStream(xmlStr
                    .getBytes(StaticValue.ENCODING))).getRootElement();
            List<Element> elements = rootElement.elements();
            Optional<Element> optional = elements.stream().filter(element -> element.getName().equalsIgnoreCase(key)).findFirst();
            if (optional.equals(Optional.empty())) {
                return null;
            } else {
                return optional.get().getTextTrim();
            }

        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * 获得导出文档设置
     *
     * @param exportType the export type
     * @return the export setting
     * @throws Exception the exception
     */
    public static ExportSettingEntity getExportSetting(String exportType) throws Exception {
        SAXReader reader = new SAXReader();

        Element e = reader.read(new File(EXPORT_CONFIG)).elementByID(exportType);
        String title = e.element("title").getStringValue();
        List<Element> sheets = e.element("sheets").elements();
        List<ExportSheetEntity> sheetEntities = new ArrayList<>(sheets.size());
        sheets.forEach(sheet ->
        {
            ExportSheetEntity sheetEntity = new ExportSheetEntity();
            String sheetName = sheet.attributeValue("name");
            String bankValue = sheet.attributeValue("bankValue");
            String totalKey = DataSwitch.convertObjectToString(sheet.attributeValue("totalKey"));
            String totalNameKey = DataSwitch.convertObjectToString(sheet.attributeValue("totalNameKey"));
            Boolean hasTotalRow = ObjectUtils.isNotEmpty(totalKey);
            Integer totalColSpan = DataSwitch.convertObjectToInteger(sheet.attributeValue("totalColSpan"));

            if (ObjectUtils.isNull(totalColSpan) || totalColSpan < 1) {
                totalColSpan = 1;
            }
            sheetEntity.setTotalKey(totalKey);
            sheetEntity.setTotalNameKey(totalNameKey);
            sheetEntity.setHasTotalRow(hasTotalRow);
            sheetEntity.setTotalColSpan(totalColSpan);

            sheetEntity.setSheetName(sheetName);
            sheetEntity.setBlankValue(bankValue);
            List<Element> rows = sheet.element("headers").elements();
            List<List<ExportTitleEntity>> titleRowsEntities = new ArrayList<>(rows.size());
            rows.forEach(row ->
            {
                List<Element> columns = row.elements("column");
                List<ExportTitleEntity> titleEntities = new ArrayList<>();
                columns.forEach(column ->
                {
                    String display = DataSwitch.convertObjectToString(column.attributeValue("display")).trim();
                    String key = DataSwitch.convertObjectToString(column.attributeValue("key")).trim();
                    String defaultValue = column.attributeValue("defaultValue");
                    String locked = column.attributeValue("locked");

                    int rowSpan = NumberUtils.toInt(column.attributeValue("rowSpan"), 0);
                    int colSpan = NumberUtils.toInt(column.attributeValue("colSpan"), 0);
                    int width = NumberUtils.toInt(column.attributeValue("width"), 30);
                    int skip = NumberUtils.toInt(column.attributeValue("skip"), 0);
                    if (skip != 0) {
                        skip++;
                    }
                    ExportTitleEntity titleEntity = new ExportTitleEntity(key, display, width, rowSpan, colSpan, skip, defaultValue);
                    titleEntity.setLocked(StringUtils.equalsIgnoreCase("true", locked));
                    titleEntities.add(titleEntity);
                });
                titleRowsEntities.add(titleEntities);
            });

            sheetEntity.setExportTitleRowsEntities(titleRowsEntities);
            sheetEntities.add(sheetEntity);
        });
        ExportSettingEntity settingEntity = new ExportSettingEntity();
        settingEntity.setExportSheetEntities(sheetEntities);
        settingEntity.setExportName(title);
        return settingEntity;
    }

}
