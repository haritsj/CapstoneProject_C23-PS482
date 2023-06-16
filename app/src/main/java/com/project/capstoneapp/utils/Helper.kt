package com.dicoding.mystoryapp.utils

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import com.project.capstoneapp.R
import java.io.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory, "$timeStamp.jpg")
}


fun rotateFile(file: File, isBackCamera: Boolean = false, reqWidth: Int, reqHeight: Int): Bitmap {
    val bitmap = decodeBitmap(file, reqWidth, reqHeight)
    val matrix = Matrix()
    val rotation = if (isBackCamera) 90f else -90f
    matrix.postRotate(rotation)
    if (!isBackCamera) {
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
    }
    val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

    return result
}

fun rotateBitmap(file: File, isBackCamera: Boolean = false, reqWidth: Int, reqHeight: Int): Bitmap {
    val bitmap = decodeBitmap(file, reqWidth, reqHeight)
    val matrix = Matrix()
    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun drawabletoFile(drawableResId: Int, context: Context): File? {
    val drawable = ContextCompat.getDrawable(context, drawableResId)
    val bitmap = drawableToBitmap(drawable)
    val file = createImageFile(context)
    saveBitmapToFile(bitmap, file)
    return file
}

fun drawableToBitmap(drawable: Drawable?): Bitmap? {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }
    val bitmap = Bitmap.createBitmap(
        drawable?.intrinsicWidth ?: 0,
        drawable?.intrinsicHeight ?: 0,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable?.setBounds(0, 0, canvas.width, canvas.height)
    drawable?.draw(canvas)
    return bitmap
}

@Throws(IOException::class)
fun createImageFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "IMG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}

fun saveBitmapToFile(bitmap: Bitmap?, file: File) {
    try {
        val outputStream = FileOutputStream(file)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.flush()
        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun reduceFileImage(file: File, maxSizeInByte: Int): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > maxSizeInByte)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun decodeBitmap(file: File, reqWidth: Int, reqHeight: Int): Bitmap {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeFile(file.path, options)

    options.inSampleSize = calculatedInSampleSize(options, reqWidth, reqHeight)

    options.inJustDecodeBounds = false
    return BitmapFactory.decodeFile(file.path, options)
}

fun calculatedInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (width: Int, height: Int) = options.run { outWidth to outHeight }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

fun calculatedCalorieNeeds(gender: String, weightKg: Double, heightCm: Double, birthDate: String): Double {
    var calorieNeeds = 0.0
    if(gender.equals("Men")) {
        calorieNeeds = ( 66.5 + (13.7 * weightKg) + (5 * heightCm) - (6.8 * calculateAge(birthDate))) * 0.5
    } else if(gender.equals("Women")) {
        calorieNeeds = ( 655 + (9.6 * weightKg) + (1.8 * heightCm) - (4.7 * calculateAge(birthDate))) * 0.5
    }
    return calorieNeeds
}

fun calculateAge(birthDate: String): Int {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val currentDate = Calendar.getInstance().time

    val dateOfBirth = dateFormat.parse(birthDate)
    val birthCalendar = Calendar.getInstance()
    if (dateOfBirth != null) {
        birthCalendar.time = dateOfBirth
    }

    var age = Calendar.getInstance().get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
    if (currentDate.before(birthCalendar.time)) {
        age--
    }
    return age
}

fun getDateWithUserLocaleFormat(): String {
    val currentDate = Date()
    val format = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    return format.format(currentDate)
}

fun formatTimeFromMinutes(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    val formattedTime = StringBuilder()

    if (hours > 0) {
        formattedTime.append(hours).append(" hr ")
    }

    if (minutes > 0) {
        formattedTime.append(minutes).append(" min")
    }

    return formattedTime.toString()
}

fun createFileFromUrl(context: Context, url: String): File? {
    try {
        val urlConnection = URL(url).openConnection()
        val inputStream = urlConnection.getInputStream()

        val outputFile = createTempFile(context)

        val outputStream = FileOutputStream(outputFile)
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        outputStream.close()
        inputStream.close()
        return outputFile
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}