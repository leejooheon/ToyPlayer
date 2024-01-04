package com.jooheon.toyplayer.domain.common

sealed class Resource<out T> {

    class Success<out T>(val value: T) : Resource<T>()

    class Failure(
        val failureStatus: FailureStatus,
        val code: Int? = null,
        val message: String? = null
    ) : Resource<Nothing>()

    object Loading : Resource<Nothing>()

    object Default : Resource<Nothing>()

    companion object {
        val longStringPlaceholder = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut tempus, sem vitae convallis imperdiet, lectus nunc pharetra diam, ac rhoncus quam eros eu risus. Nulla pulvinar condimentum erat, pulvinar tempus turpis blandit ut. Etiam sed ipsum sed lacus eleifend hendrerit eu quis quam. Etiam ligula eros, finibus vestibulum tortor ac, ultrices accumsan dolor. Vivamus vel nisl a libero lobortis posuere. Aenean facilisis nibh vel ultrices bibendum. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Suspendisse ac est vitae lacus commodo efficitur at ut massa. Etiam vestibulum sit amet sapien sed varius. Aliquam non ipsum imperdiet, pulvinar enim nec, mollis risus. Fusce id tincidunt nisl."
        val mediumStringPlaceholder = "Vanilla, Almond Flour, Eggs, Butter, Cream, Sugar"
        val shortStringPlaceholder = "Placeholder"
    }
}