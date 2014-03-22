package dataaccesslayer;

import java.sql.ResultSet;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Collection;

import domainobjects.IDHelper;

public class SQLHelper 
{
	public static String getError(Exception e)
	{
		assert(e != null);
		
		return String.format("*** SQL Error: %s", e.getMessage());
	}
	
	public static int[] parseIds(Collection<Integer> inIds)
	{
		assert(inIds != null);
		
		int [] ids = new int[inIds.size()];
		
		int i = 0;
		for(Integer id : inIds)
		{
			ids[i] = id.intValue();
			i++;
		}

		assert(i == inIds.size());
		
		return ids;
	}
	
	public static String checkWarning(Statement st, int updateCount)
	{
		String result = null;
		
		try
		{
			SQLWarning warning = st.getWarnings();
			
			result = warning != null ? warning.getMessage() : null;
		}
		catch (Exception e)
		{
			result = getError(e);
		}
				
		return result;
	}
	
	public static int getMaxIdForTable(Statement statement, String idName, String tableName)
	{
		try
		{
			String cmdString = String.format("Select MAX(%s) from %s", idName, tableName);
			ResultSet resultSet = statement.executeQuery(cmdString);
			resultSet.next();
			
			int newId = resultSet.getInt(1) + 1;

			resultSet.close();
			
			return newId;
		}
		catch(Exception ex)
		{
			System.out.println(SQLHelper.getError(ex));
			return IDHelper.getInvalidId();
		}
	}
}
