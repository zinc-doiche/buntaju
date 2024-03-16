package zinc.doiche.lib.database

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import org.bson.Document
import org.bson.conversions.Bson
import zinc.doiche.json
import kotlin.reflect.KCallable

internal infix fun KCallable<*>.eq(value: Any): Bson = Filters.eq(this.name, value)

internal infix fun KCallable<*>.gt(value: Any): Bson = Filters.gt(this.name, value)
internal infix fun KCallable<*>.gte(value: Any): Bson = Filters.gte(this.name, value)

internal infix fun KCallable<*>.lt(value: Any): Bson = Filters.lt(this.name, value)
internal infix fun KCallable<*>.lte(value: Any): Bson = Filters.lte(this.name, value)

internal infix fun Bson.and(other: Bson): Bson = Filters.and(this, other)

internal infix fun Bson.or(other: Bson): Bson = Filters.or(this, other)

internal infix fun KCallable<*>.set(value: Any): Bson = Updates.set(this.name, value)

internal fun set(value: Any): Bson = Document("\$set", Document.parse(json.writeValueAsString(value)))

