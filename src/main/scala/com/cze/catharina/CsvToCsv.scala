package com.cze.catharina

//import org.apache.spark.sql._
import com.cze.util.ConfigManager
import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.sql.{DataFrame, Row, SQLContext, SaveMode, SparkSession}

import java.sql.{Connection, ResultSet, ResultSetMetaData, SQLException}
import com.microsoft.sqlserver.jdbc.spark
import org.apache.spark.internal.Logging
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils.tableExists
import org.apache.spark.sql.functions.lit

object CsvToCsv {
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
    val in_path = args(0) // s"data/in.csv" // maprfs:///exthcp/tenant-54/fsmount/hpecp-agent.log 
    val out_path = args(1) // s"data/out.csv" //  - maprfs:///exthcp/tenant-54/fsmount/hpecp-agent.log

    val sampleDF = spark.read.format("csv")
      .option("header", "true")
      .load(in_path)
      .na.drop() //drop nulls
    sampleDF.show()

    // filter the data
    val jdbcDF1 = sampleDF
      .filter($"Numeric" > 7)
      .select("Numeric","Numeric-Suffix")
    jdbcDF1.show()

    //jdbcDF1.write.mode("overwrite").option("header", "true").csv(out_path) // distributed
    jdbcDF1.coalesce(1).write.mode("overwrite").option("header", "true").csv(out_path) // single file

  }
}