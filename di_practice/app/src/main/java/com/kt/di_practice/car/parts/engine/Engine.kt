package com.kt.di_practice.car.parts.engine

import javax.inject.Inject

/*
@Binds 어노테이션은 기본적으로 @Provides와 같은 기능을 수행한다.
다만 퍼포먼스적인 면에서 @Binds를 통해 생성되는 코드의 양이 @Provides보다 훨씬 적고
효율적이여서 가능하면 @Provides 보다는 @Binds를 사용하는 게 더 좋다.
 */
interface Engine {
    fun start()
}