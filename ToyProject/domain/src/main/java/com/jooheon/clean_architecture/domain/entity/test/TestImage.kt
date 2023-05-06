package com.jooheon.clean_architecture.domain.entity.test

data class TestImage(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val tagline: String = "",
    val tags: Set<String> = emptySet()
) {
    companion object {
        val list = listOf(
            TestImage(
                id = 1L,
                name = "Cupcake",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/pGM4sjt_BdQ",
                price = 299
            ),
            TestImage(
                id = 2L,
                name = "Donut",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/Yc5sL-ejk6U",
                price = 299
            ),
            TestImage(
                id = 3L,
                name = "Eclair",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/-LojFX9NfPY",
                price = 299
            ),
            TestImage(
                id = 4L,
                name = "Froyo",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/3U2V5WqK1PQ",
                price = 299
            ),
            TestImage(
                id = 5L,
                name = "Gingerbread",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/Y4YR9OjdIMk",
                price = 499
            ),
            TestImage(
                id = 6L,
                name = "Honeycomb",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/bELvIg_KZGU",
                price = 299
            ),
            TestImage(
                id = 7L,
                name = "Ice Cream Sandwich",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/YgYJsFDd4AU",
                price = 1299
            ),
            TestImage(
                id = 8L,
                name = "Jellybean",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/0u_vbeOkMpk",
                price = 299
            ),
            TestImage(
                id = 9L,
                name = "KitKat",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/yb16pT5F_jE",
                price = 549
            ),
            TestImage(
                id = 10L,
                name = "Lollipop",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/AHF_ZktTL6Q",
                price = 299
            ),
            TestImage(
                id = 11L,
                name = "Marshmallow",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/rqFm0IgMVYY",
                price = 299
            ),
            TestImage(
                id = 12L,
                name = "Nougat",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/qRE_OpbVPR8",
                price = 299
            ),
            TestImage(
                id = 13L,
                name = "Oreo",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/33fWPnyN6tU",
                price = 299
            ),
            TestImage(
                id = 14L,
                name = "Pie",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/aX_ljOOyWJY",
                price = 299
            ),
            TestImage(
                id = 15L,
                name = "Chips",
                imageUrl = "https://source.unsplash.com/UsSdMZ78Q3E",
                price = 299
            ),
            TestImage(
                id = 16L,
                name = "Pretzels",
                imageUrl = "https://source.unsplash.com/7meCnGCJ5Ms",
                price = 299
            ),
            TestImage(
                id = 17L,
                name = "Smoothies",
                imageUrl = "https://source.unsplash.com/m741tj4Cz7M",
                price = 299
            ),
            TestImage(
                id = 18L,
                name = "Popcorn",
                imageUrl = "https://source.unsplash.com/iuwMdNq0-s4",
                price = 299
            ),
            TestImage(
                id = 19L,
                name = "Almonds",
                imageUrl = "https://source.unsplash.com/qgWWQU1SzqM",
                price = 299
            ),
            TestImage(
                id = 20L,
                name = "Cheese",
                imageUrl = "https://source.unsplash.com/9MzCd76xLGk",
                price = 299
            ),
            TestImage(
                id = 21L,
                name = "Apples",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/1d9xXWMtQzQ",
                price = 299
            ),
            TestImage(
                id = 22L,
                name = "Apple sauce",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/wZxpOw84QTU",
                price = 299
            ),
            TestImage(
                id = 23L,
                name = "Apple chips",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/okzeRxm_GPo",
                price = 299
            ),
            TestImage(
                id = 24L,
                name = "Apple juice",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/l7imGdupuhU",
                price = 299
            ),
            TestImage(
                id = 25L,
                name = "Apple pie",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/bkXzABDt08Q",
                price = 299
            ),
            TestImage(
                id = 26L,
                name = "Grapes",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/y2MeW00BdBo",
                price = 299
            ),
            TestImage(
                id = 27L,
                name = "Kiwi",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/1oMGgHn-M8k",
                price = 299
            ),
            TestImage(
                id = 28L,
                name = "Mango",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/TIGDsyy0TK4",
                price = 299
            )
        )
    }
}