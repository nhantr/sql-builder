/*
 * InsertQueryBuilder.java
 *
 */
package ml.egoztyle.builder.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import ml.egoztyle.builder.sql.util.BuilderUtil;

/**
 * InsertQueryBuilder
 *
 */
public class InsertQueryBuilder implements QueryBuilder
{
    private final String tableName;
    private List<String> columnNameList;

    /**
     * Constructor
     *
     * @param tableName
     */
    public InsertQueryBuilder(String tableName)
    {
        this.tableName = tableName;
        this.columnNameList = new ArrayList<>();
    }

    /**
     * use columns in SQL statement
     *
     * @param columnNames column names to use
     * @return builder
     */
    @SafeVarargs
    public final <T> InsertQueryBuilder with(SerializableFunction<T, ? >... columnNames)
    {
        for (SerializableFunction<T, ? > columnName : columnNames)
        {
            this.columnNameList.add(BuilderUtil.getDBField(columnName));
        }
        return this;
    }

    /**
     * @see ml.egoztyle.builder.sql.QueryBuilder#build()
     */
    @Override
    public String build()
    {
        StringBuilder sqlQuery = new StringBuilder();
        if (CollectionUtils.isNotEmpty(columnNameList) && StringUtils.isNotBlank(tableName))
        {
            String columnList = StringUtils.join(columnNameList, ", ");
            String parameterList = StringUtils.repeat("?", ", ", columnNameList.size());

            sqlQuery.append("INSERT INTO ")
                    .append(tableName)
                    .append("(")
                    .append(columnList)
                    .append(") VALUES (")
                    .append(parameterList)
                    .append(")");
        }
        return sqlQuery.toString();
    }

}
