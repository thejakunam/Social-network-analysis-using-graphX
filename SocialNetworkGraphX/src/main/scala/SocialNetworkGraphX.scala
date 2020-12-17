import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object SocialNetworkGraphX {
  def main(args: Array[String]): Unit = {

    if (args.length != 2) {
      println("Usage: Input file outputdirectory")
      System.exit(1)
    }

    val input_file = args(0)
    val output_path = args(1)

    val sc = new SparkContext(new SparkConf().setAppName("Social Network Analysis GraphX"))
    val inputRDD = sc.textFile(input_file)
    val edgesRDD: RDD[(VertexId, VertexId)] = inputRDD.map(line => line.split("\t")).map(line =>(line(0).toInt, line(1).toInt))
    val graph = Graph.fromEdgeTuples(edgesRDD, 1)


    val outDegreeResult = graph.outDegrees.sortBy(-_._2).take(5)
    sc.parallelize(outDegreeResult).coalesce(1).saveAsTextFile(output_path+"outDegreeResult.txt")

    val inDegreeResult =  graph.inDegrees.sortBy(-_._2).take(5)
    sc.parallelize(inDegreeResult).coalesce(1).saveAsTextFile(output_path+"inDegreeResult.txt")

    val pageRankResult = graph.pageRank(0.015).vertices.sortBy(-_._2).take(5)
    sc.parallelize(pageRankResult).coalesce(1).saveAsTextFile(output_path+"pageRankResult.txt")

    val connectedComponentsResult = graph.connectedComponents().vertices.sortBy(-_._2).take(5)
    sc.parallelize(connectedComponentsResult).coalesce(1).saveAsTextFile(output_path+"connectedComponentsResult.txt")

    val triangleCountsResult= graph.triangleCount().vertices.sortBy(-_._2).take(5)
    sc.parallelize(triangleCountsResult).coalesce(1).saveAsTextFile(output_path+"triangleCountsResult.txt")
  }
}
