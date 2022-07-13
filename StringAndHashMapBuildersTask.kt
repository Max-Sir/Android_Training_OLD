import java.util.HashMap

fun <T, R> buildMutableMap(builder: HashMap<T, R>.() -> Unit): HashMap<T, R> {
    val hashMapBuilder = HashMap<T, R>()
    hashMapBuilder.builder()
    return hashMapBuilder
}

fun usage(): Map<Int, String> {
    return buildMutableMap {
        put(0, "0")
        for (i in 1..10) {
            put(i, "$i")
        }
    }
}