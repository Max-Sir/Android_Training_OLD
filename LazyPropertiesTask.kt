class LazyProperty(val initializer: () -> Int) {
    var value: Int? = null
    val lazy: Int
        get() =
            if (value == null) {
                value = initializer()
                value!!
            } else value!!

}