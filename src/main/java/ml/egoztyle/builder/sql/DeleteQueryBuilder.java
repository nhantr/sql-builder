/*
 * DeleteQueryBuilder.java
 *
 */
package ml.egoztyle.builder.sql;

import org.apache.commons.lang3.StringUtils;

import ml.egoztyle.builder.sql.util.BuilderUtil;

/**
 * DeleteQueryBuilder
 *
 */
public class DeleteQueryBuilder implements QueryBuilder
{
    private final String tableName;
    private String conditionColumn;

    /**
     * Constructor
     *
     * @param tableName
     */
    public DeleteQueryBuilder(String tableName)
    {
        this.tableName = tableName;
    }

    /**
     * use columns in WHERE clause
     *
     * @param columnName
     * @return builder
     */
    public final <T> DeleteQueryBuilder where(SerializableFunction<T, ? > columnName)
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
        if (StringUtils.isNotBlank(tableName))
        {
            sqlQuery.append("DELETE FROM ")
                    .append(tableName)
                    .append(" WHERE ")
                    .append(conditionColumn)
                    .append(" = ?");
        }
        return sqlQuery.toString();
    }

}
