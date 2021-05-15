package com.hpe.catharina

//import org.apache.spark.sql._
import com.hpe.util.ConfigManager
import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.sql.{DataFrame, Row, SQLContext, SaveMode, SparkSession}

import java.sql.{Connection, ResultSet, ResultSetMetaData, SQLException}
import com.microsoft.sqlserver.jdbc.spark
import org.apache.spark.internal.Logging
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils.tableExists
import org.apache.spark.sql.functions.lit

object MssqlQuery {
  def main(args: Array[String]) = {

    // only show errors
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)
    LogManager.getRootLogger.setLevel(Level.ERROR)

    val master = ConfigManager.config.getString("spark.master")
    val appname = ConfigManager.config.getString("spark.app.name")

    val spark: SparkSession = SparkSession.builder()
      .master(master)
      .appName(appname)
      .getOrCreate();

    import spark.implicits._

    // constants
    val in_path = s"data/in.csv" // args(0) - maprfs:///exthcp/tenant-54/fsmount/hpecp-agent.log 
    val out_path = s"data/out.csv" // args(0) - maprfs:///exthcp/tenant-54/fsmount/hpecp-agent.log 
    val db_url = "jdbc:sqlserver://localhost:1433;databaseName=master" // args(1)
    val table = "dbo.sample_table"
    val user = "sa"
    val password = "Admin123"

  	val jdbcDF = spark.read.format("jdbc")
  		.option("url",  db_url)
  		.option("dbtable", table)
  		.option("user", user)
  		.option("password", password)
  		.option("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver")
  		.load()
    jdbcDF.show() // show the raw query

    // filter the data
    val jdbcDF1 = jdbcDF
      .filter($"Numeric" > 7)
      .select("Numeric","Numeric-Suffix")
    jdbcDF1.show()

    //jdbcDF1.write.mode("overwrite").option("header", "true").csv(out_path) // distributed
    jdbcDF1.coalesce(1).write.mode("overwrite").option("header", "true").csv(out_path) // single file

  }
}