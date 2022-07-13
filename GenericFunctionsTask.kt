import java.util.*

fun <T, R : MutableCollection<T>> Collection<T>.partitionTo(
    first: R,
    second: R,
    predicate: (T) -> Boolean
): Pair<R, R> {
    this.forEach {
        when {
            predicate(it) -> first.add(it)
            else -> second.add(it)
        }
    }

    return Pair<R, R>(first, second)
}

fun partitionWordsAndLines() {
    val (words, lines) = listOf("a", "a b", "c", "d e")
        .partitionTo(ArrayList(), ArrayList()) { s -> !s.contains(" ") }
    check(words == listOf("a", "c"))
    check(lines == listOf("a b", "d e"))
}

fun partitionLettersAndOtherSymbols() {
    val (letters, other) = setOf('a', '%', 'r', '}')
        .partitionTo(HashSet(), HashSet()) { c -> c in 'a'..'z' || c in 'A'..'Z' }
    check(letters == setOf('a', 'r'))
    check(other == setOf('%', '}'))
}