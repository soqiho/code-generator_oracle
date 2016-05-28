
package com.song.generator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author soqiho@126.com
 * @version 1.0
 */
public class ParserInfo {
    

    // #<tableName>
    private String tableName;
    // #<tableComment>
    private String tableComment;
    // #<sequenceName>
    private String sequenceName;
    // #<daoPackage>
    private String daoPackage;
    // #<daoName>
    private String daoName;
    private String daoVarName;
    
    // #<modelPackage>
    private String modelPackage;
    // #<modelName>
    private String modelName;
    
    private String servicePackage;
    private String serviceImplPackage;

    private String keyColName;
    
    private String subPackage;
    
    private List<Field> fields;
    
    private Field primarykeyFields;

    public ParserInfo(Connection connection, String parentPackageName, String tableName, String sequenceName)
            throws Exception {
        fields = new ArrayList<Field>();
        daoPackage = parentPackageName +".dao";
        servicePackage= parentPackageName +".service";
        serviceImplPackage= parentPackageName +".service.impl";
        //modelPackage = parentPackageName;
        modelPackage = parentPackageName+ "";
        this.tableName = tableName;
        if (sequenceName == null) {
            sequenceName = tableName + "_S";
        }
        this.sequenceName = sequenceName;
        String javaName = Utils.getJavaName(tableName);
        daoName = javaName + "Dao";
        daoVarName=daoName.substring(0,1).toLowerCase()+daoName.substring(1);
        modelName = javaName;
        
        queryDBForTable(connection, tableName);
    }

    public ParserInfo(Connection connection, String parentPackageName, String tableName, String sequenceName,String modelPackageName)
            throws Exception {
        fields = new ArrayList<Field>();
        daoPackage = parentPackageName +".dao";
        servicePackage= parentPackageName +".service";
        serviceImplPackage= parentPackageName +".service.impl";
        //modelPackage = parentPackageName;
        modelPackage = modelPackageName+ "";
        this.tableName = tableName;
        if (sequenceName == null) {
            sequenceName = tableName + "_S";
        }
        this.sequenceName = sequenceName;
        String javaName = Utils.getJavaName(tableName);
        daoName = javaName + "Dao";
        daoVarName=daoName.substring(0,1).toLowerCase()+daoName.substring(1);
        modelName = javaName;
        
        queryDBForTable(connection, tableName);
    }
    /**
     * 查询数据库生成表
     * @param connection
     * @param tableName
     * @throws SQLException
     */
    private void queryDBForTable(Connection connection, String tableName)
            throws SQLException {
        Map<String,String> colComment=new HashMap<String,String>();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select user_col_comments.column_name,user_col_comments.comments from user_col_comments "
                + "where Table_Name='"+tableName.toUpperCase()+"' order by column_name " );
        while(rs.next()){
            colComment.put(rs.getString(1), rs.getString(2));
        }
            //this.tableComment=rs.getString(2);
        statement.close();
        rs.close();
        
        statement = connection.createStatement();
        rs = statement.executeQuery("select col.column_name   from user_constraints con,  "
                + "user_cons_columns col   where con.constraint_name = col.constraint_name  and con.constraint_type='P'   and col.table_name = '"+tableName.toUpperCase()+"' " );
        if(rs.next())
            this.keyColName=rs.getString(1);
        statement.close();
        rs.close();
        
        statement = connection.createStatement();
        rs = statement.executeQuery("select user_tab_comments.table_name,user_tab_comments.comments from user_tab_comments  where"
                + " Table_Name='"+tableName.toUpperCase()+"' order by Table_Name " );
        if(rs.next())
            this.tableComment=rs.getString(2);
        statement.close();
        rs.close();
        
        statement = connection.createStatement();
        statement = connection.createStatement();
        rs = statement.executeQuery("select  user_tab_columns.COLUMN_NAME,user_tab_columns.DATA_TYPE,"
                + "user_tab_columns.DATA_LENGTH,user_tab_columns.DATA_PRECISION,user_tab_columns.DATA_SCALE "
                + "from user_tab_columns where Table_Name='"+tableName.toUpperCase()+"' order by column_name " );
        
        while(rs.next()){
            String columnName = rs.getString(1);
            String columnTypeName =rs.getString(2);
            String description =colComment.get(columnName);
            String type = columnTypeName.toUpperCase();
            
            int precision= rs.getInt(4);
            int scale=rs.getInt(5);
            
            if(columnName.equals(this.keyColName)){
                primarykeyFields= new Field(columnName, type, Utils.getPropertyKeyByColumnName(columnName),
                        columnName.equals(this.keyColName), precision, scale);
                primarykeyFields.setFieldName(columnName);
                
                primarykeyFields.setDescription(description);
            }else{
                Field field = new Field(columnName, type, Utils.getPropertyKeyByColumnName(columnName),
                        columnName.equals(this.keyColName), precision, scale);
                field.setFieldName(columnName);
                
                field.setDescription(description);
                fields.add(field);
            }
            
            
        }
        rs.close();
        statement.close();
    }

    public String getDaoName() {
        return daoName;
    }

    public String getDaoPackage() {
        return daoPackage;
    }

    public List<Field> getFields() {
        return fields;
    }

    

    public String getModelName() {
        return modelName;
    }

    public String getModelPackage() {
        return modelPackage;
    }

    

    public String getSequenceName() {
        return sequenceName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public String getTableName() {
        return tableName;
    }

    public void setDaoName(String daoName) {
        this.daoName = daoName;
    }

    public void setDaoPackage(String daoPackage) {
        this.daoPackage = daoPackage;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getKeyColName() {
        return keyColName;
    }

    public void setKeyColName(String keyColName) {
        this.keyColName = keyColName;
    }

    public String getSubPackage() {
        return subPackage;
    }

    public void setSubPackage(String subPackage) {
        this.subPackage = subPackage;
    }

    public Field getPrimarykeyFields() {
        return primarykeyFields;
    }

    public void setPrimarykeyFields(Field primarykeyFields) {
        this.primarykeyFields = primarykeyFields;
    }

    public String getServicePackage() {
        return servicePackage;
    }

    public void setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
    }

    public String getServiceImplPackage() {
        return serviceImplPackage;
    }

    public void setServiceImplPackage(String serviceImplPackage) {
        this.serviceImplPackage = serviceImplPackage;
    }

    public String getDaoVarName() {
        return daoVarName;
    }

    public void setDaoVarName(String daoVarName) {
        this.daoVarName = daoVarName;
    }

}
