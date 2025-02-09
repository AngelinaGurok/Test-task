// Game.kt
import kotlin.math.floor
import kotlin.random.Random


const val TIMESTAMPS_COUNT = 50000
const val PROBABILITY_SCORE_CHANGED = 0.0001
const val PROBABILITY_HOME_SCORE = 0.45
const val OFFSET_MAX_STEP = 3

data class Score(val home: Int, val away: Int) //очки?? хранит два инта
//инт хом и инт эвэй (эвэй как офсет?)
data class Stamp(val offset: Int, val score: Score)//перемещение? метка? шаг? штамп?
//хранит инт офсет - сдвиг и очки?

fun generateGame(): Array<Stamp> {//генерирует игру? возвращает массив штампов?
    val stamps = Array(TIMESTAMPS_COUNT) { _ -> Stamp(0, Score(0, 0)) }
    //создает неизменный массив размером таймстэмпскоунт и инициализирует его
    // объектами stamp c нулевыми значениями
    var currentStamp = stamps[0]
    //текущий стэмп - первый стэмп массива
    //stamp[0] : offset = 0, Score.home = 0, Score.away=0
    for (i in 1 until TIMESTAMPS_COUNT) {
        currentStamp = generateStamp(currentStamp)
        //передаем в generateStamp предыдущий элемент массива
        //генерируем на его основе текущий
        stamps[i] = currentStamp
        //помещаем в массив
    }
    return stamps
}

fun generateStamp(previousValue: Stamp): Stamp {
    val scoreChanged = Random.nextFloat() > 1 - PROBABILITY_SCORE_CHANGED
    //булевая перменная, если рандом больше, чем 0,9999, то true < 0,008%
    val homeScoreChange = if (scoreChanged && Random.nextFloat() > 1 - PROBABILITY_HOME_SCORE) 1 else 0
    //homeScoreChange = 1 если scoreChanged true и рандомный флот > 0,55,
    // если что-то из этого false, то homeScoreChanged = 0
    // (чаще всего homeScoreChanged = 0, 1 примерно 3-4 раза в 50000, 0,008%)
    val awayScoreChange = if (scoreChanged && homeScoreChange == 0) 1 else 0
    //если scoreChanged = true и homeScore = 0, то awayScore = 1, иначе 0
    val offsetChange = (floor(Random.nextFloat() * OFFSET_MAX_STEP) + 1).toInt()
    //offsetChange = это сумма 1 и
    // рандомного числа от 0 до 1 невключительно, умноженного на 3, округленного в меньшую сторону
    //таким образом offsetChange может быть равен 1, 2 или 3
    if(scoreChanged){
        println("homeScoreChange: $homeScoreChange awayScoreChange: $awayScoreChange")
        println(previousValue.score.home + homeScoreChange)
        println(previousValue.score.away + awayScoreChange)
        println("offset: " + (previousValue.offset + offsetChange) )

    }
    return Stamp(
        //создаем экземпляр класса Stamp и вызываем конструктор
        previousValue.offset + offsetChange,
        //передаем сумму значения offset из переданного класса и значения offsetChange
        //для первой итерации это 0 + 1, 2 или 3
        Score(
            previousValue.score.home + homeScoreChange,
            //сумма значения Score.home из переданного в функцию класса и значения
            //homeScoreChange (0 или 1), для первой итерации будет 0 или 1
            previousValue.score.away + awayScoreChange
            //сумма прошлого Score.away и awayScoreChange (0 или 1)
        )
    )
    //возвращаем экземпляр класса Stamp
}


fun getScore(gameStamps: Array<Stamp>, offset: Int): Score {

    val index = gameStamps.binarySearch(Stamp(offset, Score(0, 0)), compareBy { it.offset })

    if(index < 0){
        throw NoSuchElementException("No stamp with offset $offset exists.")
    } else return gameStamps[index].score
}
fun main(){
    val stampsArray = generateGame()
    println(stampsArray[49999].offset)
    val offset = readln().toInt()
    try {
        val score = getScore(stampsArray, offset)
        println("Score for offset $offset = home: ${score.home} away: ${score.away}")
    } catch (exception: NoSuchElementException){
        println(exception.message)
    }

}