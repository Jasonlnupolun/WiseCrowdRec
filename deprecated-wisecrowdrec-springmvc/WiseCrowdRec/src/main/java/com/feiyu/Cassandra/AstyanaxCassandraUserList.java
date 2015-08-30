/**
 * @author feiyu
 */
package com.feiyu.Cassandra;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
//import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.feiyu.utils.GlobalVariables;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;

public class AstyanaxCassandraUserList {
  //	private static Logger LOG = Logger.getLogger(AstyanaxCassandraUserList.class.getName());
  private static ColumnFamily<String, String> CF_AST_UL;
  private final String _columnFamilyName;

  public AstyanaxCassandraUserList(String columnFamilyName) {
    this._columnFamilyName = columnFamilyName;
  }

  public void initialSetup () {
    CF_AST_UL = ColumnFamily
        .newColumnFamily(this._columnFamilyName,
          StringSerializer.get(),  // Key Serializer
          StringSerializer.get()) ;  // Column Serializer 
  }

  public void insertDataToDB(String rowKey, String new_oauth_token, String new_oauth_token_secret, String screen_name) 
      throws ConnectionException, InterruptedException, ExecutionException {
    MutationBatch mb = GlobalVariables.KS_AST.prepareMutationBatch();//The mutator is not thread safe

    mb.withRow(CF_AST_UL, rowKey) //rowKey = user_id
    .putColumn("new_oauth_token", new_oauth_token)
    .putColumn("new_oauth_token_secret", new_oauth_token_secret)
    //		.putColumn("user_id", user_id)
    .putColumn("screen_name", screen_name);

    // no asynchronous feature
    @SuppressWarnings("unused")
    OperationResult<Void> result = mb.execute();
  }

  public String[] queryWithUserID(String user_id) throws ConnectionException {
    String[] oauthInfo = new String[4];
    ColumnList<String> columns = GlobalVariables.KS_AST.prepareQuery(CF_AST_UL)
        .getKey(user_id)
        .execute()
        .getResult();
    oauthInfo[0] = user_id;
    for (Column<String> column : columns) {
      if (column.getName().equals("new_oauth_token")) {
        oauthInfo[1] = column.getValue(StringSerializer.get());
      } else if (column.getName().equals("new_oauth_token_secret")) {
        oauthInfo[2] = column.getValue(StringSerializer.get());
      } else if (column.getName().equals("screen_name")) {
        oauthInfo[3] = column.getValue(StringSerializer.get());
      }
    }
    return oauthInfo;
  }

  public static void main(String[] argv) 
      throws NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, ClassNotFoundException, TimedOutException, URISyntaxException, IOException, TException, ConnectionException, InterruptedException, ExecutionException {
    GlobalVariables.AST_CASSANDRA_MNPLT= new AstyanaxCassandraManipulator("wcrCluster","wcrkeyspace","wcrPool","localhost",9160);
    GlobalVariables.AST_CASSANDRA_MNPLT.initialSetup();

    GlobalVariables.AST_CASSANDRA_UL = new AstyanaxCassandraUserList("userlist");
    GlobalVariables.AST_CASSANDRA_UL.initialSetup();
    GlobalVariables.AST_CASSANDRA_UL.insertDataToDB("user_id", "new_oauth_token", "new_oauth_token_secret", "screen_name");
    String[] oauthInfo = GlobalVariables.AST_CASSANDRA_UL.queryWithUserID("user_id");
    for (String item: oauthInfo){
      System.out.println(item);
    }
  }
}
