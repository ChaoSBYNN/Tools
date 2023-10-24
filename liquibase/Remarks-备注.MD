# Remarks-备注

新增列时列备注会同步表表备注

```
<addColumn tableName="person_monitor_range">
    <column name="range_status" type="tinyint(1)" remarks="布控开关状态 0 关闭 1 开启"/>
</addColumn>
```

使用下面`setColumnRemarks`标签

```
<addColumn tableName="person_monitor_range">
    <column name="range_status" type="tinyint(1)"/>
</addColumn>
<setColumnRemarks tableName="person_monitor_range" columnName="range_status" remarks="布控开关状态 0 关闭 1 开启"/>
```
