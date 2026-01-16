package bapale.rioc.unizon.data.mappers

import bapale.rioc.unizon.data.remote.dto.ProductDto
import bapale.rioc.unizon.data.remote.dto.RatingDto
import bapale.rioc.unizon.domain.model.Product
import bapale.rioc.unizon.domain.model.Rating

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        rating = rating.toDomain()
    )
}

fun RatingDto.toDomain(): Rating {
    return Rating(
        rate = rate,
        count = count
    )
}