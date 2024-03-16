package zinc.doiche.lib.database

import com.mongodb.client.model.Filters
import org.bson.conversions.Bson
import kotlin.reflect.KCallable

internal infix fun KCallable<*>.eq(value: Any): Bson = Filters.eq(this.name, value)