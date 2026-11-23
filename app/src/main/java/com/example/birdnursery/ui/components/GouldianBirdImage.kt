package com.example.birdnursery.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.birdnursery.data.model.Bird
import com.example.birdnursery.data.model.GouldianGenetics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Каталог картинок фенотипов амадина Гульда из assets/genetics/images. */
object GouldianImageCatalog {
    private const val DIR = "genetics/images"

    @Volatile
    private var available: Set<String>? = null
    private val bitmapCache = HashMap<String, ImageBitmap>()

    fun availableFiles(context: Context): Set<String> =
        available ?: context.assets.list(DIR)?.toSet().orEmpty().also { available = it }

    fun load(context: Context, file: String): ImageBitmap? {
        synchronized(bitmapCache) { bitmapCache[file] }?.let { return it }
        if (file !in availableFiles(context)) return null
        val bitmap = context.assets.open("$DIR/$file").use { BitmapFactory.decodeStream(it) }
            ?: return null
        val image = bitmap.asImageBitmap()
        synchronized(bitmapCache) { bitmapCache[file] = image }
        return image
    }
}

@Composable
fun GouldianAssetImage(file: String?, contentDescription: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(initialValue = null, file) {
        value = file?.let {
            withContext(Dispatchers.IO) { GouldianImageCatalog.load(context.applicationContext, it) }
        }
    }
    val bmp = bitmap
    if (bmp != null) {
        Image(
            bitmap = bmp,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("🐦", fontSize = 32.sp)
        }
    }
}

@Composable
fun GouldianBirdImage(
    headGenotype: String,
    breastGenotype: String,
    bodyGenotype: String,
    sexLabel: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val context = LocalContext.current
    val available = remember { GouldianImageCatalog.availableFiles(context.applicationContext) }
    val sex = GouldianGenetics.sexOf(sexLabel)
    val file = remember(headGenotype, breastGenotype, bodyGenotype, sex) {
        GouldianGenetics.imageFile(headGenotype, breastGenotype, bodyGenotype, sex, available)
    }
    GouldianAssetImage(
        file = file,
        contentDescription = contentDescription,
        modifier = modifier
    )
}

@Composable
fun GouldianBirdImage(
    bird: Bird,
    modifier: Modifier = Modifier,
    contentDescription: String? = bird.name
) {
    GouldianBirdImage(
        headGenotype = bird.headGenotype,
        breastGenotype = bird.breastGenotype,
        bodyGenotype = bird.bodyGenotype,
        sexLabel = bird.sex,
        modifier = modifier,
        contentDescription = contentDescription
    )
}
