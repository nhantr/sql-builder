/*
 * UpdateQueryBuilder.java
 *
 */
package ml.egoztyle.builder.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import ml.egoztyle.builder.sql.util.BuilderUtil;

/**
 * UpdateQueryBuilder
 *
 */
public class UpdateQueryBuilder  implements QueryBuilder
{
    private final String tableName;
    private List<String> updateColumnList;
    private String conditionColumn;

    /**
     * Constructor
     *
     * @param tableName
     */
    public UpdateQueryBuilder(String tableName)
    {
        this.tableName = tableName;
        this.updateColumnList = new ArrayList<>();
    }

    /**
     * use columns in SET clause
     *
     * @param columnNames column names to use
     * @return builder
     */
    @SafeVarargs
    public final <T> UpdateQueryBuilder with(SerializableFunction<T, ? >... columnNames)
    {
        for (SerializableFunction<T, ? > columnName : columnNames)
        {
            this.updateColumnList.add(BuilderUtil.getDBField(columnName));
        }
        return this;
    }

    /**
     * use columns in WHERE clause
     *
     * @param columnName
     * @return builder
     */
    public final <T> UpdateQueryBuilder where(SerializableFunction<T, ? > columnName)
    {
        this.conditionColumn = BuilderUtil.getDBField(columnName);
        return this;
    }

    /**
     * @see ml.egoztyle.builder.sql.QueryBuilder#build()
     */
    @Override
    public String build()
    {
        StringBuilder sqlQuery = new StringBuilder();
        if (CollectionUtils.isNotEmpty(updateColumnList) && StringUtils.isNotBlank(tableName))
        {
            sqlQuery.append("UPDATE ")
                    .append(tableName)
                    .append(" SET ")
                    .append(StringUtils.join(updateColumnList, " = ?, "))
                    .append(" = ?")
                    .append(" WHERE ")
                    .append(conditionColumn)
                    .append(" = ?");
        }
        return sqlQuery.toString();
    }

}
