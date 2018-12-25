package cn.edu.gxust.jiweihuang.scala.filature.bave

import cn.edu.gxust.jiweihuang.scala.utils._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.math.{pow, sqrt}
import scala.util.control.Breaks._

/**
  * The package object of sizes package
  */
package object sizes {

  /**
    * The linear density of filament、silk or yarn.
    *
    * @param weight the weight of filament、silk or yarn,the units is g.
    * @param length the length of filament、silk or yarn,the units is m.
    */
  case class LinearDensity(weight: Double, length: Double)

  /**
    * 1 dtex = 1 g / 10000 m
    */
  object Dtex extends LinearDensity(1.0, 10000.0) {
    override def toString = s"Dtex($weight,$length)"
  }

  /**
    * 1 tex = 1 g / 1000 m
    */
  object Tex extends LinearDensity(1.0, 1000.0) {
    override def toString = s"Tex($weight,$length)"
  }

  /**
    * 1 denier = 1 g / 9000 m
    */
  object Denier extends LinearDensity(1.0, 9000.0) {
    override def toString = s"Denier($weight,$length)"
  }

  /**
    * The size of filament、silk or yarn.
    *
    * @param value the size value of filament、silk or yarn.
    * @param units the size units of filament、silk or yarn.
    */
  final class Size(val value: Double, val units: LinearDensity) {

    /** Automatically generated code by IDEA */
    def canEqual(other: Any): Boolean = other.isInstanceOf[Size]

    /** Automatically generated code by IDEA */
    override def equals(other: Any): Boolean = other match {
      case that: Size =>
        (that canEqual this) &&
          value == that.value &&
          units == that.units
      case _ => false
    }

    /** Automatically generated code by IDEA */
    override def hashCode(): Int = {
      val state = Seq(value, units)
      state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }

    /** Override the toString method */
    override def toString: String = units match {
      case Tex => s"Size($value, $Tex)"
      case Denier => s"Size($value, $Denier)"
      case Dtex => s"Size($value, $Dtex)"
      case _: LinearDensity => s"Size($value, $LinearDensity)"
    }

  }

  object Size {
    def apply(value: Double, units: LinearDensity = Dtex): Size =
      new Size(value, units)

    def unapply(arg: Size): Option[(Double, LinearDensity)] =
      if (arg == null) None else Some(arg.value, arg.units)

    /**
      * convert units of size to dtex.
      */
    def toDtex(size: Size): Size = size.units match {
      case Tex => new Size(size.value * Dtex.length / Tex.length, Dtex)
      case Denier => new Size(size.value * Dtex.length / Denier.length, Dtex)
      case _ => new Size(size.value, Dtex)
    }

    /**
      * convert units of size to tex.
      */
    def toTex(size: Size): Size = size.units match {
      case Dtex => new Size(size.value * Tex.length / Dtex.length, Tex)
      case Denier => new Size(size.value * Tex.length / Denier.length, Tex)
      case _ => new Size(size.value, Tex)
    }

    /**
      * convert units of size to denier.
      */
    def toDenier(size: Size): Size = size.units match {
      case Tex => new Size(size.value * Denier.length / Tex.length, Denier)
      case Dtex => new Size(size.value * Denier.length / Dtex.length, Denier)
      case _ => new Size(size.value, Denier)
    }
  }

  /**
    * The size sequence of cocoon filament.
    *
    * @param data  The data of size sequence
    * @param group the sizes group which sizes belongs to.
    */
  class Sizes(private val data: List[Double], val group: SizesGroup = null)
    extends Iterable[Double] {

    //Ensure the parameter {data.length >=2}.
    if (data.length < 2) throw new IllegalArgumentException(
      s"Expected the parameter {data:List[Double]} of which length is equal or greater than 2, but got {data.length = ${data.length}}")

    /** The initial size of sizes */
    def initial: Double = data.head

    /** The terminal size of sizes */
    def terminal: Double = data.last

    /** The index of maximal size in sizes
      * the first index of max size will be returned if there are many max size in sizes.
      */
    def maxIndex: Int = {
      var make_index = 0
      breakable {
        for (i <- 0 until length) {
          if (data(i) == max) {
            make_index = i
            break()
          }
        }
      }
      make_index
    }

    /** The maximal size of sizes */
    def max: Double = data.max

    /** The index of minimal size in sizes
      * the last index of min size will be returned if there are many min size in sizes.
      */
    def minIndex: Int = {
      var make_index: Int = 0
      for (i <- 0 until length) {
        if (data(i) == min) make_index = i
      }
      make_index
    }

    /** The minimal size of sizes */
    def min: Double = data.min

    /** The variance of sizes */
    def vari: Double = varip

    /** standard deviation of sizes */
    def std: Double = stdp

    /** standard deviation of sizes which be as population  */
    def stdp: Double = sqrt(varip)

    /** The variance of sizes which be as population */
    def varip: Double = devsq / length

    /** The length of sizes */
    def length: Int = data.length

    /** The sum of squares of deviations of sizes */
    def devsq: Double = {
      var make_sum = 0.0
      for (d <- data) make_sum = make_sum + pow(d - average, 2.0)
      make_sum
    }

    /** The average of sizes */
    def average: Double = sum / length

    /** The sum of sizes */
    def sum: Double = data.sum

    /** standard deviation of sizes which be as sample  */
    def stds: Double = sqrt(varis)

    /** The variance of sizes which be as sample */
    def varis: Double = devsq / (length - 1)

    /** The variance of sizes which be calculated by simplified approach */
    def vari2: Double = (sumsq - length * pow(average, 2)) / length

    /** The sum of squares of sizes */
    def sumsq: Double = {
      var make_sum = 0.0
      for (d <- data) make_sum = make_sum + pow(d, 2.0)
      make_sum
    }

    /**
      * This method is attend to be indexer.
      * for example:
      * {{{
      *   val sizes = new Sizes(2.5::2.7::3.0::2.9::2.8::2.6::2.4::2.3::2.2::Nil)
      *
      *   println(sizes(0)=${sizes(0)}    --->   sizes(0) = 2.5
      * }}}
      */
    def apply(index: Int): Double = data(index)

    def +(sizes: Sizes): Sizes = {
      val make_array = Array[Double](length)
      for (i <- 0 until length) {
        make_array(i) = sizes(i) + data(i)
      }
      new Sizes(make_array.toList)
    }

    def -(sizes: Sizes): Sizes = {
      val make_array = Array[Double](length)
      for (i <- 0 until length) {
        make_array(i) = sizes(i) - data(i)
      }
      new Sizes(make_array.toList)
    }

    def *(sizes: Sizes): Sizes = {
      val make_array = Array[Double](length)
      for (i <- 0 until length) {
        make_array(i) = sizes(i) * data(i)
      }
      new Sizes(make_array.toList)
    }

    def /(sizes: Sizes): Sizes = {
      val make_array = Array[Double](length)
      for (i <- 0 until length) {
        make_array(i) = data(i) / sizes(i)
      }
      new Sizes(make_array.toList)
    }

    def **(n: Double): Sizes = {
      val make_array = Array[Double](length)
      for (i <- 0 until length) {
        make_array(i) = pow(data(i), n)
      }
      new Sizes(make_array.toList)
    }

    override def toString: String =
      if (group == null) s"Sizes(null,$data)" else s"Sizes(${group.name},$data)"

    override def iterator: Iterator[Double] = data.iterator

    override def equals(other: Any): Boolean = other match {
      case that: Sizes =>
        (that canEqual this) &&
          group == that.group &&
          data == that.data
      case _ => false
    }

    override def canEqual(other: Any): Boolean = other.isInstanceOf[Sizes]

    override def hashCode(): Int = {
      val state = Seq(group, data)
      state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }
  }

  object Sizes {
    def apply(group: SizesGroup, data: Double*): Sizes =
      new Sizes(data.toList, group)

    def apply(data: Array[Double], group: SizesGroup): Sizes =
      new Sizes(data.toList, group)

    def apply(data: Seq[Double], group: SizesGroup): Sizes =
      new Sizes(data.toList, group)

    def apply(data: String, separator: String = "\\s+",
              ignore: Boolean = true, group: SizesGroup = null): Sizes =
      apply(data.split(separator), ignore = ignore, group = group)

    def apply(data: Array[String], ignore: Boolean, group: SizesGroup): Sizes = {
      val tem: ArrayBuffer[Double] = new ArrayBuffer[Double]()
      if (ignore) {
        for (d <- data) yield {
          if (parse[Double](d.trim).isDefined) tem.+=(parse[Double](d.trim).get)
        }
      } else {
        for (d <- data) yield {
          if (parse[Double](d.trim).isEmpty) throw new RuntimeException(s"The parameter {$d} can not be cast to double.")
          else tem.+=(parse[Double](d.trim).get)
        }
      }
      new Sizes(tem.toList, group)
    }

    def unapply(arg: Sizes): Option[(List[Double], SizesGroup)] =
      if (arg == null) None else Some(arg.data, arg.group)
  }

  /**
    * The group of size sequence.
    *
    * @param name        The name of sizes group.
    * @param testLengthM The length of filament which be test for
    *                    getting linear density value (filament size),default is 112.5 m.
    * @param units       The unit of filament size,default is dtex.
    */
  class SizesGroup(val name: String, val testLengthM: Double = 112.5,
                   val units: LinearDensity = Dtex) extends Iterable[Sizes] {

    private val sizesMap = new mutable.TreeMap[Int, Sizes]()

    //Initializing counter,ensure the key of sizes map start from 0.
    SizesGroup.counter = 0

    def count: Int = sizesMap.size

    def +=(data: String, separator: String = "\\s+", ignore: Boolean = true): SizesGroup = {
      sizesMap.+=((SizesGroup.counter, Sizes(data, separator = separator, ignore = ignore, group = this)))
      SizesGroup.counter = SizesGroup.counter + 1
      this
    }

    def +=(sizes: Sizes): SizesGroup = {
      sizesMap.+=((SizesGroup.counter, sizes))
      SizesGroup.counter = SizesGroup.counter + 1
      this
    }

    def apply(id: Int): Sizes = get(id)

    def get(id: Int): Sizes = {
      if (sizesMap.keySet.contains(id)) sizesMap(id)
      else throw new IllegalArgumentException(s"The parameter {id:Int = $id} is not in this sizes group.")
    }

    override def toString: String = {
      var tem: String = "\n"
      sizesMap.foreach(s => tem = tem + s + "\n")
      s"SizesGroup($name,$testLengthM,$units)$tem"
    }

    override def iterator: Iterator[Sizes] = sizesMap.valuesIterator

    //The Statistical characteristic for sizes group
    def lengthSum: Int = {
      var make_sum: Int = 0
      for (sizes <- this) make_sum = make_sum + sizes.length
      make_sum
    }

    def lengthAverage: Double = lengthSum / count

    def lengthVari: Double = {
      var make_sum = 0.0
      for (sizes <- this) make_sum = make_sum + pow(sizes.length - lengthAverage, 2.0)
      make_sum / count
    }

    def lengthStd: Double = sqrt(lengthVari)

    def sum: Double = {
      var make_sum: Double = 0.0
      for (sizes <- this) make_sum = make_sum + sizes.sum
      make_sum
    }

    def average: Double = sum / lengthSum

    def vari: Double = {
      variW + variB
    }

    def variW: Double = {
      var make_sum: Double = 0.0
      for (sizes <- this) make_sum = make_sum + sizes.vari * sizes.length
      make_sum / lengthSum
    }

    def variB: Double = {
      var make_sum: Double = 0.0
      for (sizes <- this) make_sum = make_sum + pow(sizes.average - average, 2.0) * sizes.length
      make_sum / lengthSum
    }

    def std: Double = sqrt(vari)

    def stdW: Double = sqrt(variW)

    def stdB: Double = sqrt(variB)


    def initialAverage: Double = {
      var make_sum = 0.0
      for (sizes <- this) make_sum = make_sum + sizes.initial
      make_sum / count
    }

    def initialVari: Double = {
      var make_sum = 0.0
      for (sizes <- this) make_sum = make_sum + pow(sizes.initial - initialAverage, 2.0)
      make_sum / count
    }

    def initialStd: Double = sqrt(initialVari)

    def terminalAverage: Double = {
      var make_sum = 0.0
      for (sizes <- this) make_sum = make_sum + sizes.terminal
      make_sum / count
    }

    def terminalVari: Double = {
      var make_sum = 0.0
      for (sizes <- this) make_sum = make_sum + pow(sizes.terminal - terminalAverage, 2.0)
      make_sum / count
    }

    def terminalStd: Double = sqrt(terminalVari)


    def averageAverage: Double = {
      var make_sum = 0.0
      for (sizes <- this) make_sum = make_sum + sizes.average
      make_sum / count
    }

    def averageVari: Double = {
      var make_sum = 0.0
      for (sizes <- this) make_sum = make_sum + pow(sizes.average - averageAverage, 2.0)
      make_sum / count
    }

    def averageStd: Double = sqrt(averageVari)

  }

  object SizesGroup {

    private var counter: Int = 0

    def apply(name: String, testLengthR: Int = 100, units: LinearDensity = Dtex): SizesGroup =
      new SizesGroup(name, testLengthR * MeterPerRevolution, units)

    def apply(name: String, testLengthR: Int, units: LinearDensity,
              separator: String, ignore: Boolean, data: String*): SizesGroup = {
      val sg = new SizesGroup(name, testLengthR * MeterPerRevolution, units)
      data.foreach(d => sg += (d, separator, ignore))
      sg
    }

    def apply(name: String, testLengthM: Double, units: LinearDensity,
              separator: String, ignore: Boolean, data: String*): SizesGroup = {
      val sg = new SizesGroup(name, testLengthM, units)
      data.foreach(d => sg += (d, separator, ignore))
      sg
    }

    def apply(name: String, testLengthR: Int, units: LinearDensity, data: Sizes*): SizesGroup = {
      val sg = new SizesGroup(name, testLengthR, units)
      data.foreach(d => sg += d)
      sg
    }

    def apply(name: String, testLengthM: Double, units: LinearDensity, data: Sizes*): SizesGroup = {
      val sg = new SizesGroup(name, testLengthM, units)
      data.foreach(d => sg += d)
      sg
    }


    def apply(name: String, testLengthR: Int, units: LinearDensity, data: Array[Sizes]): SizesGroup = {
      val sg = new SizesGroup(name, testLengthR, units)
      data.foreach(d => sg += d)
      sg
    }

    def apply(name: String, testLengthM: Double, units: LinearDensity, data: Array[Sizes]): SizesGroup = {
      val sg = new SizesGroup(name, testLengthM, units)
      data.foreach(d => sg += d)
      sg
    }

  }

  final val MeterPerRevolution = 1.125

  final val TEST10010Average: Sizes = Sizes("2.77 3.16 3.24 3.25 3.24 3.05 3.02 2.60 2.35 2.01 1.81 1.71")
  final val TEST10030Average: Sizes = Sizes("2.44 2.75 2.90 2.86 2.72 2.57 2.50 2.11 1.93 1.75 1.59")
  final val ZJJX10050Average: Sizes = Sizes("3.53 3.50 3.33 3.15 2.88 2.58 2.27 1.95 1.77 1.55 1.80 1.48")
  final val GXHX10050Average: Sizes = Sizes("3.72 3.84 3.75 3.62 3.38 3.06 2.56 2.14 1.87 1.72 1.07")
  final val GXLC50210Average: Sizes = Sizes("3.17 	3.29 	3.33 	3.26 	3.17 	3.00 	2.84 	2.58 	2.36 	2.10 	1.90 	1.71 	1.58 	1.44 	1.33 	1.30 	1.29 	1.23 	1.26 	1.17")
  final val GXRA50200Average: Sizes = Sizes("2.89 	3.03 	3.05 	2.99 	2.89 	2.78 	2.60 	2.41 	2.13 	1.91 	1.66 	1.51 	1.34 	1.25 	1.14 	1.11 	1.10 	1.08 	0.85 	1.53")

  final val TEST10010: SizesGroup = SizesGroup("TEST10010", 100, Dtex, "\\s+", false,
    "2.91	3.17	3.38	3.14	3.07	3.25	3.02	2.82	2.66	2.17	2.11",
    "3.01	3.01	3.23	3.42	3.72	3.5	3.5	2.25	2.82	2.61	2.24	1.71",
    "3.28	3.86	4.02	3.94	3.7	3.46	3.46	2.9	2.27	2.06	1.82",
    "2.9	3.3	3.28	3.21	3.12	2.98	2.98	2.7	2.29	1.99	1.84",
    "2.28	3.27	3.22	3.1	3.17	2.75	2.75	2.38	2.34	2.06",
    "3	2.98	2.85	3.25	3.15	3.37	3.37	2.84	2.27	1.81	1.63",
    "2.09	3.23	3.23	3.22	3.22	3.04	3.04	2.68	2.24	1.91	1.58",
    "2.81	3.06	3.33	3.22	3.14	2.47	2.47	2.38	2.03	1.82	1.78",
    "2.7	2.84	2.84	3.18	3.22	2.89	2.89	2.48	2.08	1.66	1.38",
    "2.69	2.86	3	2.86	2.84	2.75	2.75	2.57	2.46	2.01	1.9"
  )

  final val TEST10030: SizesGroup = SizesGroup("TEST10030", 100, Dtex, "\\s+", false,
    "3.25	3.5	3.5	3.5	3.25	2.75	2.5	2",
    "3	3.5	3.75	3.75	3.5	3.25	3.25	2.75	2.5	2",
    "2.25	2.75	3.5	3.5	3.5	3	3	2.75	2.5	1.75	1.62",
    "2	2.5	2.75	2.75	2.75	2.75	2.75	2.5	2.25	2	1.75",
    "2	2.75	2.75	3	2.75	2.5	2.5	2.25	2	1.75",
    "2.75	2.75	2.75	2.5	2.5	2.5	2.5	2.25	2.25	2",
    "2.25	2.5	2.5	2.75	2.25	2.25	2.25	2	1.75",
    "3	2.5	3.5	3.25	3	2.75	2.75	2.25	2	1.75",
    "2	3	3.25	3.25	3	2.75	2.75	1.75	1.5",
    "2	2.5	2.5	2.75	2.72	2.5	2.5	2.25	2",
    "3	3	3	3	3	3	3	2.5	2.25	1.5",
    "2.25	2.75	2.75	3	3	3.25	3.25	2.35	1.75",
    "2.25	2.75	3	3	2.75	2.75	2.75	2	1.75",
    "2.5	3	3.25	3.25	3	2.75	2.75	2.25	1.75	1.5",
    "3	3	3.25	3.25	3	3	3	2.5	2.5	2	1.75",
    "2.75	2.75	3	3	3	3	3	1.75	1.5",
    "2.75	3.25	3	3	3	2.5	2.5	2",
    "2.25	2.5	3	2.75	2.75	2.75	2.75	2.5	2	1.5",
    "2.5	2.75	3	2.5	2.5	2	2	1.75	1.5",
    "2.75	2.75	3	2.75	2.75	2.75	2.75	1.75	1.75",
    "2.5	2.5	2.5	2.5	2.25	2	2	1.75	1.5	1.47",
    "2.5	2.5	2.75	2.75	3	3	3	2.5	2.5	2",
    "2	2.75	2.75	3	3	2.5	2.5	2.25	2.25	1.75	1.25",
    "2.25	2.5	2.75	2.75	2.5	2.5	2.5	2	1.75",
    "2.75	3	2.75	2.75	2.25	2	1.75",
    "2.5	2.5	2.5	2.25	2.25	2.25	2	1.5	1.25",
    "1.75	2.5	2.25	2.25	1.75	1.5	1.25",
    "2.25	2.75	2.25	2	1.75	1.75	1.5	1.32",
    "1.5	1.75	2.75	2.5	2.25	2.25	2	2	1.5	1.5",
    "2.75	3	2.75	2.5	2.5	2.5	2	1.75"
  )

  final val ZJJX10050: SizesGroup = SizesGroup("ZJJX10050", 100, Dtex, "\\s+", false,
    "3.38	3.36	3.41	3.32	3.2	3.08	2.88	2.66	2.37	2.46	1.76	1.48",
    "4.21	4.53	4.37	4.02	3.86	3.34	2.51	2.2	1.87	1.8	1.68",
    "3.64	4.22	3.79	3.66	3.64	3.55	3.18	2.91	2.57	2.22	1.94",
    "3.23	3.24	2.94	3.35	3.34	3	2.82	2.31	2.18	1.88	1.8",
    "4	3.67	3.46	3.24	2.92	2.57	2.37	1.9	1.75	1.45",
    "4.28	3.89	3.46	3.29	3.11	3.03	2.5	2.12	1.59	1.28",
    "3.64	3.3	3.14	3.06	2.91	2.65	2.34	1.91	1.59	1.16",
    "4.26	4.12	4.02	3.88	3.72	3.56	3.26	2.87	2.51	2.23",
    "3.8	3.76	3.64	3.48	3.36	3.16	3.16	2.65	2.16	1.58",
    "3.7	3.32	3.14	3.04	2.87	2.62	2.35	1.92	1.6	1.18",
    "3.12	3.73	3.65	3.47	2.98	2.74	2.05	1.65",
    "2.84	2.98	2.5	2.41	2.18	1.89	1.74	1.28",
    "2.44	3.39	3.73	3.58	3.17	2.96	2.8	2.57",
    "2.65	2.52	2.18	2.2	2.2	1.88	1.77	1.59	1.01	0.98",
    "3.98	3.4	3.13	2.64	2.22	2.12	1.73	1.11	1",
    "2.99	2.89	2.83	2.44	1.97	1.64	1.42",
    "2.92	2.62	2.49	2.6	2.13	1.71	1.47	0.97",
    "3.81	3.77	3.64	3.48	3.33	3.18	3.32	2.36	2.02	1.48",
    "3.96	3.38	3.41	3.19	2.9	2.66	2.51	2.04	1.76",
    "3.38	3.2	3.11	2.99	2.7	2.23	1.65	1.34",
    "4.2	3.66	3.53	3.38	2.87	2.19	1.76",
    "3.65	3.4	3.32	3.25	2.94	2.61	1.89	1.73",
    "3.27	3.76	3.36	3.16	2.87	2.76	2.61	2.31	2.05",
    "3.55	3.42	3.22	2.89	2.57	2.36	1.92	1.58",
    "3.82	4.28	4.18	3.78	3.65	3.52	3.12	2.88	2.57",
    "3.86	4.26	4.31	4.1	3.9	3.94	3.61	2.58",
    "3.16	3.38	3.08	2.83	2.53	1.42	1.46	1.26",
    "3.56	3.89	3.62	3.15	2.75	2.31	1.94	1.62",
    "3.97	3.78	3.74	3.41	3.08	2.84	2.47	2.09	0.96",
    "2.71	2.64	2.39	2.28	2.2	1.9	1.8	1.76	1.37	0.98",
    "3.57	3.54	3.4	3.16	2.61	2.05	1.62	1.48",
    "3.82	3.7	3.39	3.17	2.85	2.48	2.2	1.89",
    "3.5	3.26	2.91	2.56	2.21	1.85	1.58	1.31	1.43	1.08",
    "4.22	4.05	3.8	3.54	3.26	3.12	2.77	2.57	1.96",
    "3.75	3.64	3.43	3.24	3.24	2.84	2.47	2.27	2.04",
    "2.96	2.6	2.47	2.6	2.28	1.8	1.64	1.24",
    "3.4	3.08	2.9	2.78	2.55	2.36	1.94	1.59",
    "3.18	3.28	3.53	3.36	3.04	2.78	2.38	1.75",
    "2.57	4.18	3.16	2.94	2.76	2.6	2.22	1.68",
    "3.86	4.27	4.31	4.15	3.79	3.94	3.55	2.58",
    "2.46	3.36	3.67	3.6	3.27	2.88	2.75	2.47",
    "2.83	3	2.57	2.42	2.19	2.08	2.64	1.58",
    "3.76	3.44	3.33	3.19	2.85	2.47	2	1.73",
    "3.99	3.64	3.36	3.45	3.16	2.47	2.13	1.76	1.37",
    "3.08	2.89	2.81	2.47	2.18	1.65	1.42",
    "3.39	3.19	3.09	2.85	2.68	2.21	1.62	1.36",
    "3.8	3.78	3.72	3.4	2.99	2.83	2.37	2.14	1.21",
    "4.47	3.24	2.95	2.76	2.39	2.12	1.75",
    "3.88	3.36	3.41	3.21	2.77	2.55	2.38	2.04	1.65",
    "3.8	3.54	3.31	3.01	2.89	2.45	1.43"
  )

  final val GXHX10050: SizesGroup = SizesGroup("GXHX10050", 100, Dtex, "\\s+", false,
    "4.19	4.42	4.33	4.27	3.88	3.29	2.67	2.25	1.91	1.68",
    "4.26	4.44	4.73	4.2	3.83	3.43	2.8	2.48	2.25	1.9",
    "3.55	3.88	3.68	3.45	3.34	3.27	3.12	3.02	2.73	2.36",
    "4.04	3.8	3.98	4.01	3.86	3.59	3.16	2.85	2.09	1.64",
    "3.15	2.57	3.54	3.26	3.12	2.89	2.76	2.52	1.91	1.53	1.07",
    "4.02	3.86	3.69	3.77	3.44	3.14	3.02	2.96	2.85	2.67",
    "3.35	3.38	3.33	3.23	3.08	2.99	2.57	2.11	1.67	1.16",
    "3.59	3.88	3.83	3.62	3.32	3.13	2.95	2.03	1.54	1.11",
    "4.15	4.04	3.55	3.8	3.58	3.54	3.43	3.02	2.4	2.04",
    "3.52	3.56	3.56	3.28	3.05	3.01	2.67	2.22	1.62	1.56",
    "3.8	4.09	3.97	3.86	3.73	3.66	3.21	2.52	1.97",
    "3.28	3.61	3.48	3.21	2.92	2.64	2.2	1.76	1.48",
    "3.22	3.83	3.73	3.74	3.5	3.28	3.11	2.86	2.44",
    "3.46	3.51	3.39	3.18	3.03	2.98	2.4	1.82	1.58",
    "3.48	3.72	3.4	3.33	3.2	2.91	2.25	1.64	1.45",
    "3.59	3.64	3.48	3.28	3.17	2.73	2.2	1.7	1.34",
    "3.17	3.32	3.34	3.26	2.92	2.84	2.68	2.18	1.5",
    "3.8	4.2	3.94	3.67	3.25	2.74	1.71	1.48	1.03",
    "3.91	4.34	4.36	4.1	3.78	3.74	3.41	2.66	1.93",
    "3.51	3.57	3.5	3.46	3.26	2.96	2.2	2.33	1.98",
    "4.21	4.36	4.18	4.15	4.08	3.82	3	2.6",
    "4.34	4.65	4.59	4.44	4.21	3.98	3.4	2.4",
    "3.98	4.27	3.98	3.97	3.88	3.73	3.14	2.12",
    "2.55	2.89	2.81	2.83	2.79	2.61	2.39	1.92	1.43	1.4",
    "4.12	4.57	4.28	3.96	3.83	3.03	2.37",
    "3.55	3.88	3.9	4.01	3.72	3.05	2.13	1.5",
    "3.78	4.39	4.31	4.03	3.71	3.53	3.22	2.58	2.05	1.73",
    "3.91	3.81	3.95	3.61	3.87	3.81	2.82	1.98",
    "3.56	3.93	3.69	3.6	3.31	3.32	3.04	2.43",
    "3.63	3.64	3.32	3.35	3.43	3.3	2.82	2.55	2.26	1.61",
    "3.65	3.61	4.57	4.37	3.99	3.73	3.13	2.34",
    "4.81	4.44	4.22	4	3.59	3.18	2.51	1.55",
    "4	3.83	3.49	3.7	2.57	1.75	1.27",
    "3.44	3.69	3.7	3.64	3.66	3.48	2.68	1.96",
    "3.96	3.66	3.54	3.42	3.29	3.08	2.68	1.95	1.49",
    "3.79	3.69	3.23	3.27	2.88	2	1.46",
    "4.72	3.96	3.59	3.16	2.61	1.82",
    "3.99	4.55	3.79	3.24	3.17	2.48	2.15	1.96",
    "3.56	3.94	3.95	3.92	3.66	3.15	1.98	2.26",
    "3.76	3.42	3.41	3.35	3.18	2.68	2.01	1.44",
    "3.84	4	3.95	3.8	3.64	3.1	2.71	2.18",
    "3.63	3.6	3.47	3.48	3.28	2.96	1.8",
    "3.66	3.8	3.66	3.51	3.18	3.3	2.78	2.34",
    "2.88	2.98	2.84	2.75	2.69	2.71	2.35	1.84",
    "3.66	3.62	3.94	3.88	3.69	3.28	2.5	1.9",
    "3.51	3.56	3.53	3.45	3.32	2.9	2.11	1.27",
    "3.57	4.02	4.05	3.88	3.48	3.07	2.13	1.48",
    "3.42	4.03	4.04	3.91	3.67	3.09	2.57",
    "3.81	4.1	3.55	3.18	2.47	2.02	1.78",
    "3.65	3.58	3.39	3.18	2.88	2.35	1.83	1.22"
  )

  final val GXLC50210: SizesGroup = SizesGroup("GXLC50210", 50, Dtex, "\\s+", false,
    "3.31	3.34	3.09	2.99	2.99	2.74	2.52	2.17	1.78	1.39	1.35	1.07	0.85",
    "3.88	4.34	3.98	4.37	4.01	3.8	3.83	2.45	2.45	1.88	1.7	1.42",
    "2.2	2.42	2.56	2.63	2.71	2.52	2.57	2.49	2.42	2.31	2.24	2.24	1.92	1.78	1.64",
    "4.21	4.2	3.66	4.37	4.37	3.84	3.56	3.02	2.77	2.03	1.94	1.6",
    "1.74	1.78	2.13	2.31	2.35	2.38	2.38	2.6	2.35	2.45	2.1	2.03	1.67	1.67	1.64",
    "3.09	3.13	3.27	3.2	3.38	3.16	3.34	3.13	2.95	2.7	2.49	1.88	1.71	1.42	1.32",
    "3.2	3.2	3.38	3.16	3.13	3.06	3.16	3.09	3.02",
    "3.13	2.81	2.77	2.81	2.74	2.56	2.6	2.49	2.38	1.85	1.69",
    "3.45	3.16	3.38	3.09	3.16	3.02	3.13	2.95	2.99	2.84	2.88	2.74	2.51	2.13	2.1	1.81",
    "2.13	2.38	2.45	2.46	2.63	2.45	2.51	2.35	2.31	1.81	1.53",
    "3.31	3.45	3.7	3.59	3.53	3.13	2.63	2.6	2.17	1.78	1.74	1.49",
    "2.95	3.02	3.38	3.41	3.48	3.24	3.2	3.13	3.09	3.02	3.02	2.31	2.03	1.24",
    "3.52	3.59	3.95	3.77	3.59	3.31	3.24	2.77	1.78",
    "2.88	2.54	2.49	2.45	2.44	2.31	2.2	2.24	2.24	2.17	2.26	1.53	1.49",
    "3.06	3.27	3.13	3.09	2.95	2.81	2.24	1.92	1.49	1.49	1.32",
    "3.13	3.09	2.95	2.99	2.94	2.81	2.77	2.74	2.7	2.51	2.35	1.92	1.78",
    "4.59	4.49	4.8	4.27	4.44	3.91	3.84	3.06	2.74	2.13	1.88	1.49	1.32",
    "3.34	3.27	3.09	3.09	3.06	2.88	2.52	2.45	1.96	1.88	1.42	1.39	1.07",
    "3.41	3.34	3.38	2.99	2.84	2.63	2.67	2.38	2.1	1.74	1.71	1.42	1.3",
    "3.16	3.06	3.45	3.16	3.09	2.92	2.84	2.6	2.45	1.92	1.67	1.46	1.39",
    "3.59	2.88	2.84	2.77	2.49	2.54	2.42	2.46	2.1	1.96	1.6	1.49	1.32	1.24	1.21	1.1",
    "2.92	2.88	2.74	2.7	2.74	2.38	2.31	2.03	1.81	1.53	1.39	1.14",
    "2.67	2.81	2.88	2.95	2.95	3.06	3.13	3.09	3.09	3.09	2.92	2.67",
    "2.86	3.16	3.09	2.84	2.77	2.52	2.49	2.42	2.35	2.24	2.13	2.1	1.99",
    "3.02	3.09	3.41	3.25	3.8	3.52	3.8	3.45	3.48	3.06	2.99	2.35	2.2	1.71	1.67",
    "3.52	3.27	3.7	3.48	3.66	3.73	3.38	3.24	3.16	2.74	2.35	1.96	1.99	1.71	1.67",
    "3.38	3.31	3.95	3.7	3.77	3.45	3.45	2.83	2.51	2.06	1.99",
    "2.7	2.74	2.63	2.6	2.7	2.45	2.42	2.24	2.24	2.13	1.99	2.03	1.99",
    "3.38	3.31	3.48	3.06	3.02	2.99	3.06	2.49	2.45	1.96	1.71	1.35	1.24	1.07",
    "2.31	2.95	2.99	3.2	3.31	3.13	3.24	2.92	2.92	2.31	1.88	1.67	1.64	1.42",
    "3.28	3.06	3.09	2.77	2.77	2.56	2.49	2.56	2.52	2.38	2.38	2.31	2.2	2.2	2.1	1.99",
    "3.24	3.31	3.27	3.41	2.92	2.99	2.67	2.52	2.13	2.06	1.67	1.67	1.28",
    "4.09	3.95	3.7	3.63	3.7	3.31	2.63	2.52	2.13	2.06	1.78	1.67	1.67	1.24",
    "3.45	3.48	3.73	3.59	3.61	3.06	3.04	2.52	2.49	2.06	2.04	1.71	1.67	1.32",
    "3.77	3.45	3.45	3.27	3.41	3.2	3.02	2.77	2.7	2.24	2.2",
    "3.06	2.74	2.97	2.88	2.99	2.81	2.84	2.63	2.52	2.35	2.31	2.06	1.96	1.67	1.56	1.35	1.28	1.21",
    "3.06	3.13	3.61	3.63	3.41	3.31	3.27	2.84	2.67	2.6	1.81	1.49",
    "3.63	3.31	3.06	3.02	2.88	2.81	2.92	2.63	2.6	2.24	1.99	1.46	1.28	1.03",
    "3.09	3.06	3.16	2.95	2.92	2.77	2.67	2.63	2.52	2.42	2.35	2.31	2.03",
    "3.02	3.13	3.38	3.16	3.14	2.77	2.74	2.38	2.2	1.92	1.74	1.39	1.35",
    "4.02	3.88	3.88	3.84	3.63	3.31	3.16	2.95	2.88	2.35	2.17	1.96	1.81	1.71	1.14",
    "2.77	2.92	3.02	3.06	3.16	2.95	2.84	2.49	2.31	1.96	1.6	1.49	1.5	1.24	1.17	0.92",
    "3.16	3.2	3.48	3.38	3.45	3.45	2.95	2.95	2.84	2.31	2.28	1.81	1.71	1.32	1.17",
    "4.12	4.23	4.41	3.77	3.31	3.13	3.13	2.77	2.45	2.31	1.78",
    "2.31	2.6	2.92	3.02	3.02	3.06	2.81	2.67	2.77	2.35	2.28	1.78	1.71	1.42",
    "3.77	3.34	2.99	2.84	2.67	2.63	2.56	2.7	2.13	1.99	1.92	1.89	1.53	1.42",
    "2.84	3.13	3.7	3.66	3.91	3.56	3.56	2.92	2.84	2.13	2.06	1.6	1.56",
    "2.31	2.56	2.95	2.95	3.24	3.27	3.31	3.16	3.09	2.63	2.63	2.2	2.2	1.78	1.64	1.39",
    "2.52	2.7	3.02	2.95	2.84	2.74	2.67	2.2	1.96",
    "2.49	2.52	2.7	2.77	2.92	2.74	2.67	2.56	2.49	2.03	1.88	1.56	1.39	1.14	1.1	0.82",
    "2.81	3.06	3.09	3.13	3.38	3.41	3.52	3.52	3.34	3.09	2.99	2.45	2.1",
    "3.91	4.09	4.12	3.86	3.77	3.66	3.52	3.41	3.24	2.6	2.2",
    "3.09	2.95	2.95	2.77	2.6	2.45	2.35	1.88	1.46	1.42	1",
    "2.45	2.88	3.02	3.48	3.59	3.48	3.38	3.2	3.13	2.63	2.56",
    "2.95	2.95	2.92	2.73	2.74	2.74	2.67	2.45	2.45	2.2	2.17	1.85	1.67	1.35",
    "3.63	3.64	3.66	3.96	3.8	3.27	3.13	2.35	2.17	1.74	1.6	1.6",
    "2.99	3.24	3.41	3.09	3.06	3.02	2.81	2.7	2.74	2.7	2.56	2.45	2.03	1.96	1.67	1.56	1.35	1.32	1.14",
    "2.77	3.27	3.66	3.45	3.38	3.06	2.77	2.38	2.2	1.88	1.88	1.49	1.46	1.21",
    "2.49	2.63	2.63	2.56	2.63	2.56	2.42	2.35	2.28	2.24	2.1	1.92	1.85	1.67	1.56	1.28	1.17",
    "3.59	3.7	3.66	3.73	3.48	3.52	3.31	3.16	3.24	2.95	2.84	2.56",
    "3.09	3.27	3.38	3.52	3.45	3.31	3.09	3.13	2.77	2.67	2.28	2.13	1.74	1.71	1.35	1.17	0.89",
    "2.38	2.88	2.95	3.09	3.06	3.06	2.99	2.92	2.95	2.88	2.52	2.13	1.64	1.53",
    "3.48	3.2	3.27	3.24	3.06	3.06	2.99	2.84	2.74	2.45	2.28	1.85	1.78	1.53",
    "3.06	3.27	3.63	3.59	3.47	3.38	3.24	2.92	2.81	2.24	2.06	1.67	1.49",
    "2.67	2.95	3.41	3.52	3.63	3.34	3.38	3.06	2.99	2.7	2.68	2.24",
    "2.74	2.88	3.02	3.13	3.13	3.09	2.95	2.95	2.92	2.95	2.7	2.7	2.28",
    "2.84	3.13	3.02	3.16	3.06	2.88	2.99	2.88	2.77	2.67	2.49	2.45	2.2	2.17	1.96	1.92	1.71	1.64	1.39",
    "2.03	2.95	3.13	3.45	3.2	3.09	2.77	2.67	2.13	2.06	1.74	1.64	1.39	1.35",
    "3.88	4.12	4.41	4.05	3.77	3.06	2.38	1.81	1.67	1.32	1.14",
    "2.77	2.88	3.2	2.84	2.84	2.45	2.2	1.96	1.6	1.46	1.39",
    "2.7	2.88	2.99	3.13	3.02	2.88	2.74	2.6	2.06	1.78	1.49	1.42",
    "3.13	3.34	3.2	3.38	3.34	3.2	3.09	3.02	2.63	2.51	2.06	1.92	1.56	1.35",
    "3.13	3.09	3.11	3.16	2.95	2.81	1.85	1.88	1.78",
    "3.02	3.09	3.24	3.02	2.99	2.7	2.67	2.24	2.13	1.85	1.74	1.49	1.39	1.24	1.24",
    "2.56	2.77	2.92	2.81	2.84	2.56	2.38	1.92	1.81	1.53	1.39	1.1	1	0.82",
    "3.95	3.59	3.63	3.27	3.16	2.81	2.49	1.99	1.64	1.46	1.39	1.21	1.07",
    "3.84	3.52	3.45	3.13	3.16	2.88	2.81	2.24	2.28	1.81	1.67	1.42	1.32",
    "3.2	3.16	3.06	3.02	2.99	2.56	2.45	2.06	1.81	1.49	1.39	1.14	1.03	0.92",
    "2.99	2.81	2.81	2.84	2.92	2.42	2.45	2.35	2.06	2.13	1.96	1.71	1.64",
    "3.06	3.13	3.45	3.38	2.84	2.92	2.74	2.38	2.35",
    "2.81	2.88	2.84	2.77	2.56	2.45	2.24	2.2	1.81	1.8	1.6	1.44	1.14",
    "2.77	3.06	3.09	3.31	3.52	3.34	3.41	2.99	3.02	2.67	2.35	1.71	1.56	1.56	1.28	1.24",
    "2.93	2.99	3.35	3.09	3.09	2.84	2.89	2.42	2.24	1.85	1.92	1.56	1.42	1.16	0.89",
    "2.86	2.95	3.45	3.48	3.63	3.66	3.7	3.16	2.67	2.1	1.99	1.85	1.78	1.51	1.35	1.03",
    "3.09	3.09	3.02	2.92	2.92	2.88	2.79	2.81	2.56	2.45	2.42	2.13	2.06	1.6",
    "4.34	4.52	4.48	4.02	3.95	3.34	2.95	2.17	1.96	1.67	1.46",
    "3.24	3.27	3.38	2.95	2.92	2.56	2.44	2.38	2.17	2.03",
    "3.24	3.57	4.05	3.95	3.91	3.91	3.8	3.27	2.99	2.2	1.92	1.6",
    "2.74	2.88	3.06	2.77	2.77	2.45	2.45	2.28	2.17	2.13	2.19	1.96	1.88	1.65	1.53	1.24",
    "2.81	2.7	2.42	2.45	2.42	2.38	2.17	2.1	1.81	1.49	1.28	1.17	0.92",
    "2.7	2.74	2.92	2.95	2.92	2.81	2.67	2.45	2.35	2.2	1.78	1.53	1.32	1.07",
    "2.99	3.38	3.48	3.52	3.48	3.24	3.09	2.77	2.2	2.03	1.85	1.71	1.24",
    "3.27	3.52	3.59	3.38	3.34	3.31	3.16	3.13	3.13	2.99	2.99	2.52",
    "3.66	3.8	3.45	3.41	3.06	3.02	2.52	2.1	1.71	1.46	1.14	0.89",
    "2.74	2.92	2.92	3.02	2.99	2.77	2.63	2.17	1.99	1.67	1.56	1.42	1.39	1.24	0.92",
    "2.95	2.95	2.88	2.7	2.52	2.31	1.78	1.78	1.6	1.49	1.35	1.03",
    "2.77	2.92	2.92	2.84	2.81	2.67	2.63	2.45	2.38	2.2	2.2	1.92	1.71	1.42	1",
    "3.41	3.88	4.09	4.27	4.2	4.23	3.84	3.48	2.92	2.77	2.49	2.2	1.92	1.81",
    "3.56	3.59	3.59	3.16	3.06	2.74	2.38	1.96	1.78	1.35",
    "3.77	3.48	3.2	3.13	3.02	2.74	2.74	2.52	2.28	1.96	1.6	1.39	1.1",
    "2.95	3.02	3.13	3.24	2.92	2.81	2.49	2.24	1.71	1.53	1.21",
    "3.16	3.43	3.41	3.63	3.68	3.22	3.13	3.02	2.84	2.63	2.13	1.78",
    "2.92	2.84	2.88	2.81	2.63	2.56	2.6	2.52	2.49	2.38	2.31	2.13	1.99	1.92	1.32	1.28	1.28",
    "2.92	3.2	3.06	2.95	2.77	2.67	2.45	2.42	2.1	1.92	1.71	1.64	1.39	1.1	0.96",
    "3.06	3.52	3.59	3.8	3.47	3.48	3.34	3.31	3.24	3.2	2.7	2.24	1.92	1.53",
    "2.49	2.74	2.7	2.68	2.63	2.56	2.49	2.38	2.35	2.31	2.24	1.85	1.6	1.46	1.42",
    "2.99	3.13	3.16	3.2	3.09	3.09	2.99	2.92	2.9	2.67	2.2	1.81	1.64	1.39	1.1",
    "2.35	2.49	2.52	2.49	2.63	2.67	2.6	2.6	2.6	2.45	2.45	2.2	1.96	1.74	1.49",
    "2.77	2.84	2.74	2.52	2.45	2.38	2.38	2.2	2.17	2.13	2.06	1.99	1.85	1.71	1.56	1.39",
    "2.67	2.88	2.84	2.92	2.92	2.77	2.7	2.56	2.38	2.06	1.74	1.53",
    "3.95	4.23	3.95	3.88	3.52	3.16	2.84	2.28	2.06	1.78	1.74	1.56	1.32",
    "3.24	3.59	3.34	3.34	3.24	3.2	2.95	2.6	2.13	1.78	1.6	1.17	0.96",
    "2.95	3.13	3.06	3.02	2.92	2.88	2.52	2.24	1.78	1.49	1.28	1.21	1.14	1.07	1",
    "3.63	3.77	4.02	3.88	3.41	3.2	3.09	2.74	2.2	2.03	1.85	1.71	1.28",
    "2.95	3.31	3.63	3.7	3.73	3.63	3.45	3.24	2.99	2.67	2.31	1.96	1.67	1.39	1.07",
    "3.09	3.13	2.95	2.95	2.88	2.84	2.77	2.74	2.45	2.13	1.85	1.67	1.42	1.24	1.14",
    "3.91	4.3	3.91	3.98	3.8	3.41	3.24	2.92	2.38	2.1	1.96	1.71	1.67	1.21",
    "3.48	3.16	3.02	3.06	2.99	2.9	2.84	2.81	2.56	2.49	2.13	1.81	1.56	1.32	1.17",
    "3.45	3.31	3.2	3.24	2.88	2.56	2.17	2.06	1.49	1.21	1",
    "4.12	4.02	3.98	3.8	3.68	3.45	3.16	2.88	2.45	2.28	1.99	1.88	1.6	1.32	1.14",
    "3.38	3.52	3.41	3.34	2.99	2.7	2.31	2.06	1.74	1.32	0.89",
    "3.27	3.13	3.09	2.99	2.74	2.67	2.56	2.45	2.2	1.92	1.81	1.6	1.49",
    "4.23	3.63	3.06	2.84	2.67	2.52	2.2	1.81	1.56	1.28	1.21",
    "3.13	3.06	2.95	2.95	2.77	2.52	2.03	1.85	1.6	1.53	1.49	1.28",
    "3.52	3.27	3.16	2.84	2.52	1.96	1.53	1.32	0.78	0.75",
    "3.45	3.06	3.02	2.95	2.6	2.49	2.45	2.13	1.74	1.6	1.53",
    "2.88	2.81	2.49	2.45	2.24	2.2	1.92	1.71	1.64	1.42	1.07",
    "2.95	3.27	3.41	3.32	3.27	3.16	2.99	2.95	2.74	2.6	2.06	1.96	1.64	1.49	1.42	1.1",
    "3.13	3.38	3.48	3.59	3.16	3.09	2.77	2.31	2.17	1.96	1.67	1.46	1.28	1.07	0.92",
    "3.66	3.34	3.06	2.92	2.88	2.92	2.56	2.24	2.1	1.78	1.64	1.42	1.32	1.14	1.03",
    "2.31	2.63	2.7	2.74	2.38	2.03	1.78	1.6	1.46	1.28",
    "2.56	2.81	2.92	3.13	2.99	2.84	2.77	2.56	2.35	2.17	1.96	1.64	1.53	1.17	1	0.89",
    "3.56	3.52	3.56	3.45	3.2	3.02	2.95	2.95	2.88	2.84	2.52	1.99	1.78	1.71",
    "4.09	3.63	3.31	2.95	2.81	2.7	2.49	2.17	1.92	1.64	1.35	0.96",
    "2.88	3.38	3.41	3.38	3.34	3.31	3.2	2.95	2.81	2.67	2.49	2.24	2.03	1.85	1.6	1.53	1.53	1.24	1.24	1.17",
    "3.24	3.52	3.41	3.27	3.24	3.02	2.67	2.52	2.35	2.03	2.03	1.78	1.67	1.64	1.24	1.17",
    "2.6	2.56	2.45	2.31	2.1	1.99	1.92	1.78	1.67	1.42	1.28	1.07",
    "2.84	3.77	3.88	4.12	4.27	4.27	3.95	3.56	2.67	2.42",
    "3.59	3.73	3.88	4.09	3.98	3.7	3.56	3.31	2.84	2.13	1.92	1.71",
    "3.73	3.8	4.2	4.12	3.95	3.7	3.73	3.2	3.02	2.49",
    "2.56	2.63	2.74	2.95	3.09	2.84	2.1	1.64",
    "3.7	3.63	3.56	3.31	3.06	2.84	2.84	2.67	2.31	2.03	1.78	1.35",
    "3.27	3.16	3.09	2.7	2.35	2.24	2.13	1.71	1.6	1.53	1.49	1.32	1.17	1.14",
    "3.31	3.24	3.02	2.95	2.95	2.84	2.67	2.52	2.28	2.03	1.81	1.6	1.42	1.21	0.96",
    "2.95	2.92	2.88	2.84	2.81	2.84	2.77	2.74	2.52	2.31	1.99	1.78	1.67	1.6	1.42	1.24	1.14	1.03",
    "3.5	3.77	3.84	3.84	3.77	3.48	3.24	2.84	2.28	1.88	1.6	1.35	1",
    "3.06	3.56	3.52	3.48	3.31	3.24	3.09	2.77	2.49	2.13	1.96	1.81	1.6	1.56	1.42	1.07",
    "3.32	3.13	2.95	2.92	2.84	2.67	2.52	2.56	1.99	1.64	1.35	1.24	1.14	1.03	1",
    "3.06	3.02	3.06	3.09	3.31	3.2	3.16	3.06	2.88	2.56	2.13	1.78	1.56	1.42	1.32",
    "3.48	3.59	3.63	3.52	3.48	3.38	3.31	3.02	2.49	1.92	1.74	1.42	1.24",
    "3.09	3.48	3.13	3.02	3.06	3.06	2.84	2.63	2.06	1.78",
    "2.77	2.84	2.88	2.81	2.81	2.84	2.84	2.77	2.63	2.24	1.67	1.56	1.32	1.07	0.96",
    "2.74	3.09	3.27	3.31	3.13	3.09	2.81	2.7	2.49	2.28	1.99	1.96	1.78	1.64	1.6	1.46	1.32",
    "3.13	3.31	3.41	3.45	3.31	3.34	3.16	3.02	2.81	2.17	1.6	1.42	1.28	1.24	1.17",
    "4.23	4.3	4.09	3.93	3.48	3.13	2.56	2.06	1.78	1.35	1.07",
    "3.13	3.06	2.88	2.88	2.92	2.56	2.49	2.45	2.13	2.1	2.06",
    "2.92	3.45	3.48	3.63	3.38	3.31	3.27	2.84	2.49	1.88	1.56	1.46	1.24",
    "2.84	2.88	3.2	3.24	3.06	3.02	2.77	2.74	2.52	2.42	2.1	1.81	1.64	1.42	1.35	1.32",
    "2.92	2.99	3.09	2.99	2.92	2.88	2.74	2.35	2.13	1.81	1.56	1.21	0.96",
    "2.77	2.74	2.7	2.67	2.6	2.42	2.13	2.03	1.96	1.85	1.78	1.6",
    "3.48	3.98	4.05	3.7	3.59	3.56	3.27	3.2	3.09	2.81	2.49	2.24	2.03	1.6",
    "2.67	3.27	3.48	3.34	3.31	3.27	3.2	2.92	2.81	2.49	2.13	1.71	1.35",
    "2.6	2.88	3.02	2.95	2.88	2.77	2.67	2.52	2.42	2.24	1.99	1.64	1.17",
    "3.48	3.63	3.34	3.06	2.74	2.56	2.17	1.42	1.14	1.1	0.92	0.89",
    "2.84	2.81	2.67	2.56	2.52	2.28	1.99	1.99	1.96	1.96	1.85	1.88	1.74	1.67	1.6	1.53	1.14",
    "3.59	3.27	2.99	2.92	2.88	2.77	2.6	2.52	2.42	2.24	1.99	1.85	1.71	1.49	1.39	1.28	1.14	0.96",
    "3.09	3.06	3.27	3.2	3.13	2.99	2.88	2.52	2.31	2.03	1.96	1.85	1.78	1.49	1.21	0.96",
    "3.48	3.56	3.41	3.38	3.27	3.27	2.38	1.96	1.67",
    "3.34	3.56	3.41	3.34	3.2	3.06	2.95	2.74	2.49	1.96",
    "2.2	3.41	3.41	3.16	3.09	3.02	2.84	2.77	2.56	2.31	2.1	1.92	1.14",
    "3.06	3.66	3.95	3.63	3.38	2.6	2.06	1.56	1.39	1.1	1",
    "3.13	3.27	3.24	3.09	3.09	2.81	2.6	2.17	2.2	1.74	1.39",
    "3.09	3.52	3.73	3.8	3.7	3.66	3.52	3.34	3.16	2.56	1.99	1.67	1.42	1.24	1.03	0.78",
    "3.98	4	3.98	3.95	3.95	3.95	3.8	3.52	3.09	2.35	1.88	1.6",
    "4.05	4.59	4.44	4.27	3.88	3.7	3.41	2.63	2.2	2.06	1.78	1.56	1.24",
    "2.13	2.49	2.67	2.31	2.35	2.06	1.96	1.78	1.81	1.53	1.42",
    "3.31	3.7	3.56	3.34	3.27	3.2	3.2	3.13	3.15	3.13	3.06	2.95	2.81	2.2	1.96	1.64	1.56",
    "3.52	3.66	3.7	3.59	3.48	3.13	2.84	2.6	2.42	2.03	1.78",
    "2.49	2.77	2.84	2.74	2.6	2.52	2.52	2.35	2.17	2.03	1.74	1.53	1.42	1.17",
    "3.48	3.52	3.56	3.48	3.45	3.38	3.34	3.31	3.02	2.95	2.7	2.49	1.99	1.35",
    "3.34	3.59	3.41	3.31	3.02	2.77	1.92	1.6	1.35	1.24	0.82	0.71",
    "2.56	2.52	2.42	2.38	2.35	2.1	2.03	1.78	1.71	1.53	1.49	1.28	1.1	0.85",
    "3.66	4.52	4.48	4.23	3.73	3.59	3.02	2.67	1.99	1.96	1.6	0.96",
    "2.49	3.06	3.31	3.59	3.24	3.16	3.13	3.02	2.74	2.2	1.74	1.71	1.53	1.49	1.28",
    "3.27	3.41	3.31	3.27	3.2	3.24	3.2	2.92	2.56	2.17	1.78",
    "4.41	4.27	4.2	3.8	3.59	3.06	2.67	2.06	1.78	1.21	1.14",
    "4.09	4.23	3.7	3.38	3.34	3.27	3.31	2.84	2.52	2.24	2.1	1.39",
    "3.63	3.88	3.59	3.56	3.16	3.16	2.92	2.77	2.52	2.35	2.03	1.85	1.64	1.39	1.07",
    "3.02	3.27	3.31	3.56	3.61	3.45	3.34	3.34	3.31	3.06	2.45	2.38	2.03	1.81	1.56	1.21",
    "4.34	4.3	4.16	3.88	3.8	3.52	3.45	2.84	2.56	2.24	2.03	1.88	1.78	1.35	0.85",
    "2.84	2.92	2.95	3.06	3.09	3.06	2.74	2.45	1.92	1.71	1.53	1.24",
    "3.24	3.48	3.95	3.77	3.66	3.45	3.41	2.77	2.49	1.96	1.67	1.21",
    "3.16	3.13	3.11	3.02	2.84	2.88	2.45	2.2	1.78	1.49	1.28	1.07",
    "3.24	3.2	3.45	3.24	3.09	3.02	2.84	2.81	2.84	2.63	2.38	2.06	1.92	1.67	1.49	1.21",
    "3.56	3.63	3.52	3.45	3.38	3.34	3.24	3.02	3.06	2.92	2.24	1.92	1.6	1.32",
    "3.24	3.2	3.48	3.2	3.13	3.02	2.95	2.7	2.38	2.06	1.96	1.53	1.21	0.89",
    "2.84	3.27	3.38	3.27	3.2	3.09	3.02	2.77	2.77	2.45	2.38	2.03	1.99	1.88	1.74	1.67",
    "3.84	4.69	4.69	4.12	4.05	3.66	3.45	2.81	2.7	2.38	2.06	1.24",
    "2.17	3.2	3.27	3.34	3.41	3.41	3.34	3.27	2.99	2.81	2.38	1.92	1.64	1.32",
    "3.31	3.52	3.73	3.59	3.52	3.02	2.77	2.06	1.71	1.35	1.21	0.92	0.85",
    "3.25	3.24	3.38	3.31	3.24	3.06	3.02	2.92	2.88	2.42	2.17	1.81	1.67	1.35	0.92",
    "2.81	3.06	3.09	3.27	3.34	3.06	2.99	2.24	2.03	1.96	1.74	1.53	1.42",
    "3.8	3.91	4.09	3.88	3.84	3.38	3.24	2.52	2.35	2.1	1.92	1.78	1.46",
    "4.69	5.05	4.59	4.48	4.05	3.63	2.95	2.56	2.03	1.88	1.39	0.89",
    "3.52	3.66	3.59	3.48	3.25	3.09	2.77	2.61	2.28	2.24	1.85	1.67	1.24	1.03",
    "2.95	3.13	3.34	3.13	3.16	3.02	2.84	2.06	1.6	1.24	1.21	1",
    "3.06	3.24	3.38	3.24	3.27	3.24	3.16	3.13	3.06	2.95	2.88	2.6	2.38	1.96	1.81	1.32",
    "2.95	3.41	3.7	4.02	3.52	3.48	3.24	2.88	2.13	1.85	1.53	1.35	1.14	0.96",
    "3.63	3.73	3.98	3.66	3.56	3.16	3.02	2.7	2.52	2.13	1.88	1.71	1.64	1.64",
    "3.24	3.77	3.98	3.7	3.66	3.09	3.02	2.6	2.31	1.88	1.78	1.53	1.42	1.24	0.96"
  )

  final val GXRA50200: SizesGroup = SizesGroup("GXRA50200", 50, Dtex, "\\s+", false,
    "1.88	2.19	1.85	1.99	2.03	1.92	1.78	1.81	1.56	1.56	1.39	1.56	1.3	1.32",
    "4.62	2.95	3.11	2.72	2.44	2.45	2.44	2.2	2.03	1.67	1.64",
    "2.81	3.38	2.81	2.81	2.49	2.95	2.28	2.79	1.85	1.74	1.24",
    "3.73	3.88	3.38	4.05	3.34	3.27	3.27	3.2	2.7	2.52	2.2	2.03",
    "3.95	3.77	3.73	3.59	3.41	3.38	3.41	3.16	2.88	1.78	1.24",
    "4.84	4.52	4.59	4.44	3.77	3.48	3.06	2.49	1.96	1.78	1.17	1.07	0.89	0.82",
    "3.77	3.98	3.84	3.59	3.27	3.09	2.1	2.84	2.38	2.13	1.88",
    "3.41	3.38	3.77	3.59	3.63	3.38	3.06	2.88	2.2	2.1	1.92	1.65",
    "2.84	2.99	3.06	3.24	3.13	3.16	2.74	2.7	2.49	2.49	2.2	2.2	1.81	1.67	1.28",
    "2.38	2.88	2.6	2.7	2.77	3.63	3.08	3.77	3.06	2.67	2.2	1.96	1.6	1.42	1.17",
    "2.49	2.95	3.45	3.24	3.24	2.88	2.88	2.7	2.7	2.1	1.96	1.64	1.49	1",
    "2.99	3.24	3.38	3.31	3.31	2.92	2.52	2.35	1.81	1.88	1.32	0.96",
    "3.52	3.59	3.7	3.93	3.64	3.7	3.31	3.41	3.02	2.95	2.31	1.64	1.42	1.24",
    "3.02	2.84	3.31	3.16	3.13	2.99	2.77	2.29	2.31	1.85	1.42	1.28	0.96	0.6",
    "2.67	3.27	3.34	3.13	3.34	2.81	3.16	2.2	1.78	1.53	1.42",
    "2.99	2.92	2.84	3.16	2.95	2.84	2.56	2.7	2.38	2.52	2.63	2.24	2.17	1.78",
    "2.74	2.79	2.84	2.7	2.7	2.88	2.63	2.6	2.35	2.45	1.85	1.39	1.49	1	0.71",
    "2.84	3.09	3.34	3.48	2.88	2.95	3.02	3.13	2.81	2.45	2.1	1.85	1.6	1.07	1.42	1.24	1.07",
    "2.77	3.24	3.02	3.02	2.67	2.6	2.29	2.28	2.31	3.15	1.85	2.04	2.2	2.31	0.92	1.28",
    "3.79	4.41	4.14	4.27	3.95	3.91	3.38	3.15	2.67	2.29	1.69	1.28",
    "3.41	3.7	4.27	3.75	3.64	3.84	3.13	2.9	2.52	2.42	1.88	1.99	1.17",
    "2.28	2.38	2.38	2.52	2.51	2.63	2.38	2.4	2.42	2.24	1.96	2.68	1.85	1.72",
    "2.84	2.95	3.16	3.22	3.41	2.99	2.49	2.13	1.67	1.53	1.23	1.28",
    "3.02	2.92	2.88	2.92	2.81	2.76	2.52	2.88	2.06	1.69	1.74	1.56	1.28",
    "3.66	3.38	3.31	3.06	3.13	2.7	3.09	2.49	2.81	1.53	1.14",
    "2.6	3.38	2.84	2.76	2.88	2.63	2.49	1.97	2.2	1.85	1.69	1.26	1.24	0.96	0.94",
    "2.77	3.09	3.45	3.2	3.22	2.84	2.6	2.13	1.74	1.53	1.28",
    "3.02	3.06	3.04	2.95	3.09	2.77	2.88	2.52	2.38	1.92	1.6	1.24	1.07",
    "3.4	2.99	3.45	3.38	3.34	3.27	3.13	2.95	2.88	2.38	2.03",
    "2.81	3.63	3.41	3.48	3.66	4.27	2.84	3.88	2.13	1.6",
    "3.56	3.06	2.88	2.88	2.52	2.52	2.2	2.2	1.67	1.6	1.49	1.21	1.1",
    "2.56	3.45	3.25	3.41	3.24	3.24	3.38	3.41	2.52	1.42",
    "3.38	3.34	3.31	2.77	2.81	2.56	2.52	2.31	2.28	1.88	1.64	1.46	1.39	1.21",
    "2.52	2.92	2.99	2.88	2.99	2.6	2.77	2.52	2.42	2.06	1.78	1.64	1.6	1.32	1.42",
    "3.09	3.02	2.84	2.92	2.74	3.16	2.79	2.99	2.63	2.52	2.31	1.53	1.32	1.42",
    "2.88	3.45	3.41	3.34	2.92	2.99	2.56	2.56	2.38	2.24	1.81	1.64	1.6	1.49	1.03",
    "2.49	2.99	2.84	2.92	2.81	2.84	2.7	2.52	2.56	1.88	1.32	1.03",
    "3.84	3.48	3.84	3.24	3.45	3.02	2.99	2.24	1.74	1.71	1.17	1.03	0.96	0.89",
    "2.49	2.99	3.16	3.34	2.99	3.16	2.95	2.74	2.28	1.96	1.56	1.23	0.8",
    "3.52	3.7	3.24	3.24	2.77	2.74	2.13	1.99	1.6	1.49	1.32	1.21	0.78",
    "3.52	3.77	3.88	3.95	3.95	3.16	3.24	2.56	2.38	2.13	1.74	1.64	1.85",
    "4.52	4.8	4.52	4.16	4.66	2.24	2.95	1.88	2.17	1.28	0.89	1.32",
    "3.29	3.48	3.45	3.34	3.06	3.16	2.84	2.7	1.81	1.71	1.46",
    "2.74	2.56	2.77	2.77	2.84	2.7	3.13	2.63	1.85	1.24	0.89",
    "2.56	3.06	3.32	3.16	2.9	2.72	2.72	2.42	2.74	2.31	2.06	1.6	1.49",
    "2.81	3.73	3.59	3.34	3.31	3.09	2.72	2.56	2.38	2.35	1.78	1.53	1.24	1.1	0.96	0.96	0.96	0.96",
    "2.45	2.6	2.58	2.56	2.56	2.74	2.84	2.81	2.67	2.77	2.35	2.1	1.67	1.32",
    "2.95	2.81	3.38	3.16	3.63	3.56	2.84	2.74	2.42	2.1	2.03	1.46	1.07",
    "3.06	4.2	3.41	3.88	3.41	3.66	3.13	3.45	3.13	3.45	3.13	2.74	2.35	2.31	1.81",
    "2.52	3.16	3.31	3.16	2.88	2.81	2.63	2.6	2.28	1.85	1.56	1.35	1.1	0.82",
    "2.88	3.73	2.63	3.52	3.95	3.31	3.41	2.92	1.85	2.38	1.85	1.35",
    "3.02	3.06	3.06	3.56	3.13	3.2	2.31	2.13	1.67	1.24",
    "2.67	2.84	2.99	2.88	2.92	2.9	2.74	2.95	2.74	2.52	2.03	1.6	1.28	1.24	0.89",
    "2.31	2.1	2.31	2.17	2.45	2.35	2.42	2.24	2.28	2.1	1.92	1.78	1.67	1.28	1.07	0.6",
    "3.31	3.77	3.41	3.56	3.2	3.24	2.74	2.24	1.78	1.67	1.81",
    "3.66	3.8	3.98	3.73	3.66	3.88	3.31	2.88	1.21	2.24	2.13	1.81	1.24	0.78",
    "2.88	3.56	3.91	3.88	3.7	3.73	3.27	3.34	2.81	2.63	2.74	2.6	1.96	1.74",
    "2.58	2.99	2.92	2.95	3.06	2.99	2.9	3.15	2.6	2.67	2.2	1.96	1.49	1.32	1.03	0.82",
    "2.1	2.88	3.63	4.02	3.73	3.63	4.55	3.31	2.67	1.96	1.71	1.35	1.07	0.57",
    "2.81	2.52	2.52	2.67	2.6	2.49	2.31	2.2	1.96	1.64	1.35	1.17	1.53	1.1	0.71",
    "3.16	3.31	3.24	3.06	2.99	2.99	2.74	2.95	2.38",
    "2.77	2.99	2.58	2.52	2.4	2.36	2.61	2.63	2.45	2.13	1.9	1.53	1.37	1.19	0.89	1.01",
    "2.63	2.74	2.97	2.99	3	2.88	2.36	2.63	2.42	2.24	1.85	1.71	1.16",
    "3.27	3.56	3.24	3.38	3.06	2.88	2.67	2.49	2.28	1.97	1.76	1.49	1.21	1.16	1.07",
    "2.72	2.77	2.68	2.65	2.68	2.49	2.38	2.49	2.06	2.19	1.74	1.49	1.4	1.21	0.89",
    "2.06	2.38	2.47	2.42	2.19	2.08	1.97	1.87	1.74	1.55	1.35	1.26	1.14	1.12	1.21",
    "3.06	2.63	2.92	2.84	2.84	2.92	3.09	2.56	2.35	2.06	1.64	1.07",
    "2.49	2.77	2.92	2.56	2.31	1.85	1.74	1.46	1.39	1.21	1.24	1.1	0.96",
    "2.6	2.77	2.52	2.49	2.45	2.03	1.74	1.39	1.03	1	0.82",
    "2.56	2.45	2.74	2.77	2.28	2.88	2.17	1.6	1.53	1.49	1.14	1.39	0.92	1.07",
    "2.79	3.45	3.59	3.63	2.6	1.21	1.78	2.52	2.95	0.85",
    "2.38	2.56	2.13	2.42	1.92	2.13	1.81	1.67	1.28	1.32	0.78",
    "4.02	3.56	3.63	3.73	2.99	3.2	2.92	2.92	2.45	1.99	1.67	1.74	1.56	1.71	1.07	1.39",
    "2.81	3.27	2.99	3.16	2.95	3.06	2.63	2.17	1.56	1.49	1.17	0.78	0.28",
    "2.77	2.74	2.84	2.56	2.35	2.03	1.88	1.67	1.6	1.35	1.32	0.82",
    "2.63	2.63	3.16	3.31	3.16	2.95	3.48	2.28	2.56	1.85	1.42	1.53	1.32	0.89",
    "2.31	2.74	3.2	2.95	2.92	2.42	2.38	1.78	1.46	1.28	0.53",
    "2.38	2.7	2.7	2.49	2.42	2.06	2.2	1.74	1.67	1.35	1.35	1.32	1.07	0.64",
    "3.02	2.7	3.06	2.42	2.67	2.38	2.4	1.71	1.92	1.42	1.39",
    "3.34	2.81	3.02	2.92	2.67	2.74	2.67	2.13	1.92	1.53	1.28	0.96	0.68",
    "2.1	3.13	2.38	1	2.67	1.81	1.32	0.89	1.39	0.71",
    "3.8	2.56	2.54	2.45	2.49	2.2	2.45	1.92	1.85	1.24	0.82	0.89	1.49",
    "3.09	3.09	3.13	2.6	2.63	2.13	2.17	1.35	1.42	1.1	0.71	0.71",
    "3.88	3.27	2.92	2.88	2.31	2.13	1.49	1.21	0.92",
    "2.92	2.88	3.2	2.77	2.77	2.67	2.31	2.1	1.99	1.78	1.46	1.1	1.6	1.12	1.07	0.75",
    "2.88	3.11	3.16	2.67	2.42	1.64	1.81	1.64	1.64	1.14	0.71",
    "2.88	2.56	2.81	2.28	2.52	2.2	1.88	1.92	2.03	1.56	1.78	1.53	1.6	1.32	1.28	0.96	0.78",
    "1.88	1.99	1.92	1.99	1.92	1.78	1.67	1.6	1.67	1.42	1.42	1.17	1.07	1",
    "1.96	2.38	2.2	2.28	2.45	2.56	2.19	1.99	1.67	1.49	1.14	0.92	0.82	1",
    "2.74	2.88	2.81	2.77	2.42	2.56	2.13	2.17	2.06	2.03	1.67	1.56	1.32	1.07	0.78	0.43",
    "2.38	2.42	2.28	2.2	2.2	2.1	1.96	1.78	1.56	1.74	1.6	1.39	1.35	1.14",
    "3.41	3.38	2.95	2.74	2.56	2.24	2.03	2.06	1.24	1.21	1.03	1.03	0.96	1",
    "2.81	2.31	2.6	2.35	2.7	2.38	3.16	2.28	2.28	2.31	1.78	1.81	1.39	1.46	1.03",
    "2.42	2.49	2.38	2.35	2.24	2.1	2.06	1.78	1.78	1.46	1.81	1.39	1.17	1.07	0.92	0.89",
    "1.96	2.49	2.52	2.24	2.03	1.74	1.49	1.42	1.17	1.1	0.96	0.85	0.71	0.5",
    "2.74	2.52	2.92	2.84	3.77	2.95	2.6	2.77	2.1	2.92	2.2	1.53",
    "2.99	2.74	2.28	2.42	2.24	2.42	1.88	2.2	1.78	1.88	1.53	1.49	1.35	1.6",
    "2.81	2.81	2.63	1.85	2.24	2.1	1.99	1.85	1.71	1.6	1.6	1.53	1.42	1.6",
    "4.62	2.6	2.81	2.74	2.56	2.31	2.1	1.71	1.85	1.49	1.74	0.92	1.28",
    "2.56	2.81	2.95	2.88	2.84	2.31	2.74	2.38	2.42	2.17	1.88	1.6	1.56	1.21	1.07	0.89	0.53",
    "2.56	2.49	2.81	2.45	2.74	2.67	2.84	2.52	2.56	2.06	2.35	2.1	1.6	1.99	1.85	1.39	1.03	0.71	0.57",
    "1.96	2.17	2.42	2.52	2.49	2.35	2.38	2.49	1.85	1.42	1.24	0.92	0.89	0.6",
    "3.48	3.73	3.8	3.77	3.73	3.27	2.84	2.31	1.99	1.6	1.35	1.1",
    "2.13	2.06	2.17	2.1	2.1	2.03	2.03	1.85	1.81	1.78	1.49	1.21	0.89	0.78",
    "1.81	2.35	2.13	1.74	1.85	2.06	1.6	1.6	1.32	1.35	1	1.28	1.07	1.1	0.92	1.03",
    "2.03	2.31	2.77	2.52	2.67	2.31	2.38	2.13	1.74	1.32	1.14	0.92	0.82	0.75	0.85",
    "2.56	2.28	2.92	2.52	2.7	2.45	2.42	2.1	2.06	1.96	1.88	1.53	1.53	1.21	0.78",
    "4.55	4.23	3.56	3.59	3.16	3.16	2.2	2.03	1.71	1.71	1.35	1.49	1.07",
    "2.84	3.16	3.41	3.41	3.2	3.02	3.02	2.7	2.38	1.88	1.67	1.42	1.32	1.03	0.78	0.43",
    "1.53	1.99	2.2	2.49	2.52	2.45	2.38	2.28	2.17	1.85	1.6	1.39	1.21	1.1	0.96	0.89	0.75	0.6",
    "2.2	2.92	3.41	3.41	3.56	3.27	3.27	2.92	2.81	2.03	1.14",
    "4.02	3.88	3.98	3.41	3.73	3.7	3.45	2.99	2.31	1.67	1.46	1.03	0.68	0.43",
    "2.13	2.67	2.99	3.02	3.16	3.09	2.7	2.13	1.78	1.42	1.03",
    "1.96	1.96	2.2	1.81	2.1	1.74	1.81	2.31	1.53	1.21	0.68",
    "2.13	2.74	2.67	2.99	2.49	3.06	1.99	2.6	2.52	1.56	1.07	0.89",
    "3.09	3.45	2.88	3.13	3.13	2.88	3.06	2.49	2.52	2.03	1.67	1.14	1.28	1.03	1.1	1.03",
    "2.35	2.38	2.52	2.52	2.35	2.13	1.96	1.81	1.53	1.35	1.14	1	0.96	0.85	0.78	0.78	0.82	0.85	0.53",
    "1.96	2.2	2.49	2.63	2.7	2.63	2.38	1.88	1.39	1.14	0.92	0.75",
    "2.45	2.31	2.74	2.31	2.74	2.31	2.49	2.2	2.13	2.03	1.88	1.53	0.82	1.1	1.07	1.1",
    "4.05	2.67	2.67	2.88	2.31	2.45	1.99	2.1	1.67	1.6	1.1	1.07	0.75	0.71",
    "2.7	2.67	3.09	2.77	3.02	2.31	2.38	2.28	2.2	1.6	1.64	1.39	1.49	1.21	1.24",
    "1.99	2.17	2.88	2.7	3.2	2.88	2.92	2.7	2.77	2.38	2.1	1.39	1.42	1.03	0.96",
    "2.99	3.06	2.81	2.74	2.45	2.28	1.99	1.78	1.6	1.49	1.32	1.14	1.03	0.6	0.36",
    "3.24	2.99	3.06	2.95	2.74	2.63	2.45	2.35	2.13	1.99	1.78	1.42	1.21	1.1	0.82",
    "2.1	2.84	2.81	2.84	2.67	2.67	2.45	2.24	2.17	2.1	1.67	1.35	1.1	0.82",
    "2.13	2.24	2.24	2.31	2.24	2.13	1.96	1.81	1.6	1.49	1.53	1.21	1.1	1.03	0.89",
    "2.67	2.63	2.74	2.45	2.17	1.88	1.6	1.49	1.24	1.07	0.89	0.75	0.5",
    "2.56	2.38	2.42	2.49	2.35	2.35	2.17	2.17	2.06	1.96	1.78	1.53	1.24	1.24	0.96",
    "3.06	3.16	2.92	2.6	2.24	1.6	1.56	1.81	1.6	1.58	1.42	1.35	1.21	1.08	0.85	0.71",
    "2.06	2.13	2.24	2.2	2.1	2.03	1.92	1.92	1.92	1.88	1.85	1.71	1.78	1.64	1.6	1.24",
    "2.17	2.31	2.42	2.17	2.2	2.03	2.03	1.88	1.74	1.71	1.6	1.6	1.42	1.42	1.24	1.1	1.28",
    "2.99	3.63	3.02	3.13	2.88	2.99	2.88	2.99	2.2	2.38	1.85	1.81	0.92",
    "2.63	2.92	2.45	2.84	2.49	2.88	2.49	2.7	2.28	2.45	1.92	2.1	1.71	1.74	1.46	1.46	1.17	1.17	0.39",
    "2.84	2.92	2.95	3.06	2.74	2.54	2.49	2.13	1.88	1.67	1.64	1.32	1.17	0.85	0.71	0.53",
    "3.34	3.41	3.24	3.09	2.7	2.56	2.31	2.24	1.78	1.67	1.56	1.51	1.32	1.14	1.03",
    "2.95	2.88	2.81	2.63	2.45	2.45	2.45	2.2	2.1	2.35	1.96	1.78",
    "2.99	3.24	3.52	3.2	2.84	2.7	2.74	1.99	1.99	1.56	1.03	0.89",
    "2.49	2.7	2.31	2.67	1.96	2.49	2.2	2.42	2.03	2.22	1.78	1.78	1.49	1.55",
    "3.09	3.66	3.06	3.02	2.42	2.49	2.17	1.78	1.32	1.39	1.14	0.85	0.78",
    "2.03	2.35	2.35	2.7	1.88	2.03	1.71	1.78	1.28	1.28	1.32	1	1.07	0.82	0.96	0.68	0.78",
    "3.09	3.31	3.38	3.27	3.13	3.02	2.77	2.67	2.2	1.74	1.35	1.28	0.96	0.85",
    "3.06	3.16	3.09	2.95	2.74	2.6	2.24	2.13	1.81	1.53	1.32	1.21	0.89	0.68",
    "1.39	1.69	1.92	2.06	1.99	2.06	1.92	1.85	1.56	1.32	1",
    "1.88	2.84	3.73	1.78	1.74	2.17	1.67",
    "2.17	2.49	2.42	2.24	1.92	1.53	1.32	1.16	0.96	0.85	0.71",
    "2.74	2.79	2.7	2.49	2.49	2.31	2.28	1.78	1.39	1.14	1.07	0.89	0.68",
    "3.13	3.16	3.38	2.7	3.09	2.88	2.74	1.81	1.71	1.53	1.32",
    "3.38	3.16	3.06	2.84	2.84	2.45	2.1	1.6	1.32	1.07	1.28	1.03	0.68	0.36",
    "2.28	2.45	2.52	2.42	2.24	2.06	1.85	1.64	1.46	1",
    "2.84	2.63	3.31	2.88	3.2	3.06	3.38	2.99	2.88	2.2	2.13	1.32	1.28	0.94",
    "2.52	3.56	3.06	3.66	2.92	3.27	2.88	2.76	2.31	1.78	1.21	1.17	1.17",
    "2.77	2.31	2.84	2.28	2.42	1.92	1.88	1.14	1.46	1.24	1.28	1.1",
    "2.7	2.84	2.67	2.92	2.6	2.84	2.28	2.13	1.74	1.53	1.21",
    "3.09	3.45	3.59	3.45	3.34	3.27	2.74	2.6	2.24	2.17	1.88	1.74	1.42	1.49	1.14",
    "3.31	3.24	3.41	3.09	3.06	3.02	2.95	2.77	2.45	2.17	1.78	1.6	1.49	1.35	1.03	0.71",
    "2.49	3.41	3.48	3.38	3.41	3.09	2.97	2.74	2.7	1.67	1.53	1.46	1.46	1.24	1.21	1.32	1.17",
    "1.96	1.99	2.35	2.38	2.31	1.99	1.85	1.49	1.39	1.14	0.89	0.71	0.53	0.32	0.21",
    "2.81	2.84	2.67	2.6	2.49	2.24	2.06	1.49	2.1	2.24	1.96	2.03	1.53	1.6	1.35	1.35	1.07	1.1",
    "1.99	2.95	3.09	3.88	3.27	3.73	3.27	3.02	2.45	2.74	2.42	2.24	1.67	1.49	1.03	0.92",
    "2.92	3.48	3.24	3.56	3.48	3.41	2.92	3.16	2.42	2.06	1.6	1.67	1.28	1.24",
    "2.17	1.88	2.13	2.31	2.31	2.45	2.1	2.2	2.1	2.52	2.2	1.85	1.74	1.28	1.32",
    "2.06	2.52	2.74	2.95	3.02	2.92	3.02	2.74	2.6	2.13	1.85	1.46	1.24	0.6	0.57",
    "1.92	2.06	2.1	2.2	2.03	2.1	1.96	1.92	1.92	1.88	1.78	1.64	1.32	1.14	0.89	0.92	0.85	0.71",
    "3.54	3.59	3.13	3.09	2.77	2.7	2.49	2.45	2.01	2.06	1.99	1.88	1.78	1.6	1.37	1.07",
    "3.56	3.27	3.84	3.24	3.84	3.25	3.59	3.06	3.09	2.35	2.28	1.85	1.83	2.17	1.72	1.42",
    "3.27	3.91	3.59	4	3.29	3.91	3.45	3.64	2.88	2.92	2.38	1.6",
    "2.95	2.67	2.77	3.06	2.84	2.81	2.81	2.81	2.38	2.67	2.1	2.36	1.99	2.2	1.83	1.96	1.6	1.74	1.49",
    "2.95	3.32	3.59	3.34	3.45	2.81	3.06	2.68	2.31	1.81	1.9	1.53",
    "2.33	2.45	2.9	3.02	2.79	2.84	2.74	2.67	2.44	2.35	2.13	2.13	2.01	1.99	1.9	1.87	1.78	1.81	1.28",
    "2.77	3.24	3.8	3.8	3.84	3.54	3.31	2.67	2.06	1.88	1.39	1.3",
    "2.56	3.34	3.7	3.86	3.77	3.52	3.2	3.02	2.51	2.2	1.85	1.71",
    "2.84	4.34	3.7	3.43	3.24	2.95	3.56	2.35	2.2	2.17",
    "2.63	3.41	3.95	4.2	3.7	4.05	3.56	3.73	2.97	3.09	2.52	2.67	2.45	2.67",
    "3.66	3.59	4.04	3.54	3.95	3.41	3.7	3.52	2.99	3.06	2.45	2.24	1.39",
    "2.86	3.13	2.74	3.41	3.2	3.48	3.08	3.31	3.09	2.56	2.31	2.03",
    "3.5	3.2	2.95	3.06	3.31	2.67	3.06	2.15	2.33	1.99	1.88	1.56	1.74	1.67",
    "4.43	4.09	3.48	3.66	3.43	3.52	3.41	3.38	2.81	2.63	2.35	2.04	1.72	1.6",
    "2.81	3.06	2.74	2.74	2.77	2.67	2.31	2.17	1.83	1.88	1.58	1.85	1.67",
    "3.24	3.38	3.16	3.24	3.16	3.2	2.26	2.1	0.75",
    "3.56	3.22	3.16	2.95	2.93	2.76	2.61	2.35	2.28	1.96	1.85	1.55	1.42	1.1",
    "3.32	3.16	3.24	2.93	2.77	2.63	2.47	2.17	1.96	1.71	1.49	1.28	1.14",
    "2.92	3.32	3.41	3.61	3.7	3.45	3.63	3.27	3.09	2.81	2.49	1.67",
    "3.48	3.48	3.59	3.7	3.88	3.63	3.63	3.38	3.34	3.15	3.02	2.77	2.7	2.49	2.45	2.24",
    "3.13	2.74	3.09	2.63	2.7	2.51	2.58	2.06	2.06	1.74	1.62	1.35	1.46	1.07	1.07",
    "2.95	3.06	3.4	3.02	3.22	3.13	3.13	2.74	2.81	2.28	2.36	1.78	1.78	1.42",
    "2.83	2.9	3.47	3.13	3.25	3.04	3.02	2.2	2.1	1.58	1.46	1.17	1.14	0.96	0.91",
    "2.88	3.59	3.24	3.63	3.06	2.99	2.74	2.95	2.52	2.1",
    "4.2	3.61	3.77	3.16	3.4	2.95	3.16	2.81	2.74	2.13	2.38	1.96	1.99	1.64	1.67	1.39	1.23",
    "2.67	2.77	2.49	2.99	2.49	2.74	2.42	2.58	2.35	2.35	1.88	1.71	1.3",
    "2.7	3.02	2.92	3.31	3.09	3.77	3.45	3.32	2.74	2.33	2.38	2.28",
    "3.59	3.95	3.38	3.52	3.24	3.48	3.13	3.47	2.84	2.99	2.44	2.45	2.06	2.08	1.85	1.72	1.44",
    "3.06	3.13	2.84	3.04	2.77	3.09	3.02	3.06	2.95	3.02	2.63	2.67	1.78	2.24	2.1",
    "3.96	4.69	4.43	4.02	4.18	4.3	4.52	3.2	1.85	1.74",
    "2.67	2.6	2.77	2.7	2.84	2.99	3.06	2.76	2.79	2.31	2.06	1.65	1.6	1.28",
    "3.48	3.56	3.31	3.31	3.16	2.97	2.58	2.06	1.49	1.64	1.49	1.28",
    "3.88	3.2	3.13	3.06	2.81	2.81	2.72	2.63	2.49	1.88	1.67	1.53	1.21	1.28	1.17	1.07",
    "3.48	3.34	3.73	3.66	3.8	3.5	2.74	2.92	2.28	2.13	1.81	1.56",
    "3.52	3.95	3.8	3.8	3.73	3.66	3.66	3.15	2.74	2.28	1.96	1.74	1.67	1.56",
    "2.92	2.93	2.95	3.02	2.88	2.84	2.31	2.74	2.67	2.49	2.2	1.96	1.67	1.56	1.24	1.39	1.17	1.1",
    "3.16	3.16	3.04	3.09	3.08	3.08	2.9	2.9	3.06	2.84	2.38	2.13	1.99	1.81	1.74	1.49	1.39"
  )
}
