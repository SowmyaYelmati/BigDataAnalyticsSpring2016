package edu.umkc.fv

import edu.umkc.fv.NLPUtils._
import edu.umkc.fv.Utils._
import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by Vikas on 3/2/2016.
  */
object FeatureVector1 {
  def main(args: Array[String]) {
    val filters = args
    // Set the system properties so that Twitter4j library used by twitter stream
    // can use them to generate OAuth credentials

    val consumer_key = "pRRfJNUzj9Cl3uMSk8abRFzjk"
    val consumer_secret = "Z6xJne8cFIzUl8QrUBo4scEifEYXQ3GK9NhSewMJT2htaMIB73"
    val access_token = "4371859274-rGf2ZsqhECB1WfN24zHWvn9D7HZIDCv8QQAXnlQ"
    val access_secret = "NjUGIbdF8bf5chFyN2khqYvxNExBOqGsmbHlQNBMYnl1o"


    // Set the system properties so that Twitter4j library used by twitter stream
    // can use them to generate OAuth credentials

    System.setProperty("twitter4j.oauth.consumerKey",consumer_key)
    System.setProperty("twitter4j.oauth.consumerSecret", consumer_secret)
    System.setProperty("twitter4j.oauth.accessToken", access_token)
    System.setProperty("twitter4j.oauth.accessTokenSecret", access_secret)
    System.setProperty("hadoop.home.dir", "C:\\winutils")
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark-Machine_Learning-Text-1").set("spark.driver.memory", "3g").set("spark.executor.memory", "3g")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    val stream = TwitterUtils.createStream(ssc, None, filters)
    stream.saveAsTextFiles("data/testing/result")
    /* val robo = stream.flatMap(status => status.getText.split(" ").filter(_.contains("Robo")))
     robo.saveAsTextFiles("data/training/Tweets_Training_data_robo.txt")
     val android2 = stream.flatMap(status => status.getText.split(" ").filter(_.contains("android")))
     android2.saveAsTextFiles("data/training/Tweets_Training_data_android2.txt")
     val psychology = stream.flatMap(status => status.getText.split(" ").filter(_.contains("psychology")))
     psychology.saveAsTextFiles("data/training/Tweets_Training_data_psychology.txt")*/
    ssc.start()
    ssc.awaitTermination(1000)
    val sc = ssc.sparkContext
    val stopWords = sc.broadcast(loadStopWords("/stopwords.txt")).value
    val labelToNumeric = createLabelMap("data/training/")
    var model: NaiveBayesModel = null
    // Training the data
    val training = sc.wholeTextFiles("data/training/*")
      .map(rawText => createLabeledDocument(rawText, labelToNumeric, stopWords))
    val X_train = tfidfTransformer(training)
    X_train.foreach(vv => println(vv))

    model = NaiveBayes.train(X_train, lambda = 1.0)

    val lines=sc.wholeTextFiles("data/testing/*")
    val data = lines.map(line => {

      val test = createLabeledDocumentTest(line._2, labelToNumeric, stopWords)
      println(test.body)
      test


    })

    val X_test = tfidfTransformerTest(sc, data)

    val predictionAndLabel = model.predict(X_test)
    println("PREDICTION")
    predictionAndLabel.foreach(x => {
      labelToNumeric.foreach { y => if (y._2 == x) {
        println(y._1)
      }
      }
    })





  }


}
